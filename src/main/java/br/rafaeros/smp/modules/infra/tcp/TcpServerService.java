package br.rafaeros.smp.modules.infra.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.rafaeros.smp.modules.device.model.enums.ProcessStatus;
import br.rafaeros.smp.modules.device.service.DeviceService;
import br.rafaeros.smp.modules.log.controller.dto.DeviceLogPayloadDTO;
import br.rafaeros.smp.modules.log.service.LogService;
import br.rafaeros.smp.modules.order.model.Order;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TcpServerService {

    private final DeviceService deviceService;
    private final LogService logService;
    private final ObjectMapper objectMapper;
    
    // Virtual Threads (Java 21+): Ideal para I/O bloqueante como Sockets. 
    // Permite milhares de conexões simultâneas com baixo custo de memória.
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    private ServerSocket serverSocket;
    private volatile boolean running = true;

    @PostConstruct
    public void startServer() {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(5050);
                log.info("🚀 TCP Server rodando na porta 5050");

                while (running && !serverSocket.isClosed()) {
                    Socket clientSocket = serverSocket.accept();
                    executor.submit(() -> handleClient(clientSocket));
                }
            } catch (IOException e) {
                if (running) log.error("Erro fatal no servidor TCP", e);
            }
        }).start();
    }

    private void handleClient(Socket deviceSocket) {
        String macAddress = null;
        String clientIp = deviceSocket.getInetAddress().getHostAddress();
        
        try {
            // Timeout de 5 minutos. Se não receber PING ou dados nesse tempo, desconecta.
            deviceSocket.setSoTimeout(15000);

            BufferedReader reader = new BufferedReader(new InputStreamReader(deviceSocket.getInputStream()));
            // Auto-flush = true é importante para garantir envio imediato
            PrintWriter writer = new PrintWriter(deviceSocket.getOutputStream(), true);

            // --- 1. HANDSHAKE INICIAL ---
            String firstLine = reader.readLine();
            
            if (firstLine == null) {
                return; // Conexão abriu e fechou sem dados
            }

            if (firstLine.startsWith("ID:")) {
                String[] parts = firstLine.split("ID:");
                if (parts.length > 1) {
                    macAddress = parts[1].trim();
                    
                    // Registra/Atualiza IP e Status
                    deviceService.registerOrUpdateDevice(macAddress, clientIp);
                    
                    writer.println("OK_CONNECTED"); // Resposta exata que o Firmware espera
                    log.info("✅ [TCP] Device conectado: {} ({})", macAddress, clientIp);
                } else {
                    writer.println("ERROR_FORMAT");
                    log.warn("⚠️ [TCP] Handshake malformado de {}: {}", clientIp, firstLine);
                    return; // Fecha socket no finally
                }
            } else {
                log.warn("⚠️ [TCP] Protocolo inválido de {}: {}", clientIp, firstLine);
                return; // Fecha socket
            }

            // --- 2. LOOP DE MENSAGENS ---
            String line;
            while ((line = reader.readLine()) != null) {
                // Atualiza "Visto por último" a cada interação para não ficar offline no dashboard
                deviceService.updateLastSeen(macAddress);
                
                line = line.trim(); // Remove espaços extras/quebras de linha

                // A. Processamento de Log (JSON)
                if (line.startsWith("{")) {
                    processJsonLog(macAddress, line, writer);
                }
                // B. Ping/Pong (Keep Alive)
                else if (line.equals("PING")) {
                    writer.println("PONG");
                } 
                // C. Requisição da Ordem Atual
                else if (line.equals("GET_ORDER")) {
                    try {
                        Order order = deviceService.getCurrentOrderEntityByMac(macAddress);
                        
                        if (order != null) {
                            writer.println("ORDER:" + order.getId() + ":" + order.getCode());
                            log.info("📤 [TCP] Enviado ORDER:{}:{} para {}", order.getId(), order.getCode(), macAddress);
                        } else {
                            writer.println("NO_ORDER");
                            log.info("📤 [TCP] Enviado NO_ORDER (Sem OP vinculada) para {}", macAddress);
                        }
                    } catch (Exception e) {
                        log.error("Erro ao buscar ordem para {}: {}", macAddress, e.getMessage());
                        writer.println("ERROR_DB");
                    }
                }
                // D. Atualização de Status (RUNNING, PAUSED, IDLE)
                else if (line.startsWith("STATUS:")) {
                    processStatusChange(macAddress, line, writer);
                }
                // E. Comando desconhecido
                else {
                    log.debug("❓ [TCP] Comando ignorado de {}: {}", macAddress, line);
                }
            }

        } catch (SocketTimeoutException e) {
            log.warn("⏰ [TCP] Timeout (sem PING) para device: {}", macAddress);
        } catch (Exception e) {
            log.error("❌ [TCP] Erro na conexão com {}: {}", macAddress, e.getMessage());
        } finally {
            // Limpeza de recursos
            if (macAddress != null) {
                log.info("🔌 [TCP] Device desconectado: {}", macAddress);
                deviceService.handleDisconnect(macAddress); // Marca como OFFLINE e IDLE
            }
            try {
                if (!deviceSocket.isClosed()) deviceSocket.close();
            } catch (IOException e) {
                // Ignora erro de fechamento
            }
        }
    }

    private void processJsonLog(String macAddress, String jsonLine, PrintWriter writer) {
        try {
            // Log raw para debug se necessário
            // log.debug("JSON recebido de {}: {}", macAddress, jsonLine);

            DeviceLogPayloadDTO payload = objectMapper.readValue(jsonLine, DeviceLogPayloadDTO.class);
            
            // Persiste no banco
            logService.registerLogFromDevice(macAddress, payload);

            log.info("📝 [TCP] Log Salvo - Device: {}, OP: {}, Qtd: {}", macAddress, payload.orderId(), payload.quantityProduced());
            writer.println("OK_LOG"); 

        } catch (JsonProcessingException e) {
            log.error("❌ [TCP] JSON Inválido de {}: {}", macAddress, jsonLine);
            writer.println("ERROR_JSON_FORMAT");
        } catch (Exception e) {
            log.error("❌ [TCP] Falha ao salvar log de {}: {}", macAddress, e.getMessage());
            // Importante: Não derrubar a conexão, avisar o device para tentar depois
            writer.println("ERROR_SAVING"); 
        }
    }

    private void processStatusChange(String macAddress, String line, PrintWriter writer) {
        try {
            String statusStr = line.split(":")[1].trim();
            ProcessStatus status = ProcessStatus.valueOf(statusStr);
            
            deviceService.updateProcessStatus(macAddress, status);
            writer.println("OK_STATUS");
        } catch (IllegalArgumentException e) {
            writer.println("ERROR_STATUS_INVALID");
        } catch (Exception e) {
            writer.println("ERROR_STATUS_PROCESS");
        }
    }

    @PreDestroy
    public void stop() throws IOException {
        running = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
        executor.shutdown();
    }
}