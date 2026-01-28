package br.rafaeros.smp.modules.infraestructure.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.rafaeros.smp.modules.device.service.DeviceService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Service
public class TcpServerService {

    @Autowired
    private DeviceService deviceService;

    private ServerSocket serverSocket;

    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    @PostConstruct
    public void startServer() {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(5050);
                System.out.println("🚀 TCP Server rodando na porta 5050");

                while (!serverSocket.isClosed()) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("⚡ Conexão TCP recebida de: " + clientSocket.getInetAddress().getHostAddress());

                    executor.submit(() -> handleClient(clientSocket));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void handleClient(Socket deviceSocket) {
        String macAddress = null;
        try {
            deviceSocket.setSoTimeout(10000);

            BufferedReader reader = new BufferedReader(new InputStreamReader(deviceSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(deviceSocket.getOutputStream(), true);
            String firstLine = reader.readLine();

            if (firstLine != null && firstLine.startsWith("ID:")) {
                String[] parts = firstLine.split("ID:");
                if (parts.length > 1) {
                    macAddress = parts[1].trim();
                } else {
                    System.out.println("❌ [TCP] Erro: ID recebido vazio ou formato incorreto.");
                    writer.println("ERROR_FORMAT");
                    deviceSocket.close();
                    return;
                }

                System.out.println("✅ [TCP] Identificado MAC: " + macAddress);

                deviceService.registerOrUpdateDevice(macAddress, deviceSocket.getInetAddress().getHostAddress());

                writer.println("OK");
            } else {
                System.out.println("❌ [TCP] Handshake inválido. Recebido: " + firstLine);
                deviceSocket.close();
                return;
            }

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("PING")) {
                    writer.println("PONG");
                } else if (line.startsWith("LOG:")) {
                    System.out.println("📝 [TCP] Log de " + macAddress + ": " + line);
                }
            }
        } catch (Exception e) {
            System.out.println("⚠️ [TCP] Erro/Desconexão (" + macAddress + "): " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (macAddress != null) {
                deviceService.setStatusOffline(macAddress);
                System.out.println("🔌 [TCP] Conexão finalizada para: " + macAddress);
            }
            try {
                deviceSocket.close();
            } catch (IOException e) {
            }
        }
    }

    @PreDestroy
    public void stop() throws IOException {
        if (serverSocket != null)
            serverSocket.close();
    }

}
