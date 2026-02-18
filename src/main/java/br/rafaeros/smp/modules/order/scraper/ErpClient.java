package br.rafaeros.smp.modules.order.scraper;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Map;

import jakarta.annotation.PostConstruct; // Importante para rodar ao iniciar
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ErpClient {
    private final ErpProperties properties;
    private Map<String, String> cookies;
    private String dynamicBaseUrl;

    @PostConstruct
    public void init() {
        log.info("Iniciando ErpClient e tentando autenticação prévia...");
        try {
            authenticate();
        } catch (Exception e) {
            log.warn("Não foi possível autenticar no ERP durante a inicialização (O ERP pode estar offline). " +
                    "Uma nova tentativa será feita na primeira requisição. Erro: {}", e.getMessage());
        }
    }

    public Document searchOrders(ErpSearchFilter filter) {
        if (this.cookies == null || this.dynamicBaseUrl == null) {
            try {
                authenticate();
            } catch (IOException e) {
                throw new RuntimeException("Falha ao conectar no ERP para autenticação inicial.", e);
            }
        }

        try {
            return executeSearchRequest(filter);
        } catch (SocketTimeoutException e) {
            log.error("Timeout ao buscar no ERP: {}", e.getMessage());
            throw new RuntimeException("O ERP demorou muito para responder. Tente novamente mais tarde.");
        } catch (IOException e) {
            log.warn("Erro na busca (possível sessão expirada). Tentando renovar sessão e buscar novamente...", e);
            try {
                this.cookies = null;
                authenticate(); 
                return executeSearchRequest(filter);
            } catch (IOException ex) {
                log.error("Falha fatal ao reconectar no ERP.", ex);
                throw new RuntimeException("Não foi possível manter a conexão com o ERP.", ex);
            }
        }
    }

    private Document executeSearchRequest(ErpSearchFilter filter) throws IOException {
        String searchUrl = this.dynamicBaseUrl + "/ordemProducao/exportarOrdens";

        log.info("Buscando ordens no ERP. URL: {}", searchUrl);

        return Jsoup.connect(searchUrl)
                .cookies(cookies)
                .userAgent("Mozilla/5.0")
                .timeout(180000)
                .maxBodySize(0)
                .ignoreContentType(true)
                .data("OrdemProducao[codigo]", safeStr(filter.getCode()))
                .data("OrdemProducao[_nomeCliente]", safeStr(filter.getClientName()))
                .data("OrdemProducao[_nomeMaterial]", safeStr(filter.getProductCode()))
                .data("OrdemProducao[status_op_id]", "Todos")
                .data("OrdemProducao[_etapasPlanejadas]", "")
                .data("OrdemProducao[forecast]", "0")
                .data("OrdemProducao[_inicioCriacao]", "")
                .data("OrdemProducao[_fimCriacao]", "")
                .data("OrdemProducao[_inicioEntrega]", safeStr(filter.getStartDeliveryDate()))
                .data("OrdemProducao[_fimEntrega]", safeStr(filter.getEndDeliveryDate()))
                .data("OrdemProducao[_limparFiltro]", "0")
                .data("pageSize", "20")
                .method(Connection.Method.GET)
                .get();
    }

    private synchronized void authenticate() throws IOException {
        if (this.cookies != null && this.dynamicBaseUrl != null) return;

        log.info("Autenticando no ERP...");

        Connection.Response loginPageResponse = Jsoup.connect(properties.getUrl())
                .userAgent("Mozilla/5.0")
                .followRedirects(true)
                .method(Connection.Method.GET)
                .execute();

        URL finalUrl = loginPageResponse.url();
        String loginUrlString = finalUrl.toString();
        this.dynamicBaseUrl = finalUrl.getProtocol() + "://" + finalUrl.getHost();
        Document loginDoc = loginPageResponse.parse();
        String csrfToken = loginDoc.select("input[name=YII_CSRF_TOKEN]").val();
        String codigoConexao = loginDoc.select("input[name='LoginForm[codigoConexao]']").val();
        if ((codigoConexao == null || codigoConexao.isEmpty()) && loginUrlString.contains("?c=")) {
            codigoConexao = loginUrlString.split("\\?c=")[1].split("&")[0];
            codigoConexao = java.net.URLDecoder.decode(codigoConexao, java.nio.charset.StandardCharsets.UTF_8);
        }

        Connection.Response authResponse = Jsoup.connect(loginUrlString)
                .userAgent("Mozilla/5.0")
                .cookies(loginPageResponse.cookies())
                .header("Origin", this.dynamicBaseUrl)
                .header("Referer", loginUrlString)
                .data("YII_CSRF_TOKEN", csrfToken != null ? csrfToken : "")
                .data("LoginForm[username]", properties.getUsername())
                .data("LoginForm[password]", properties.getPassword())
                .data("LoginForm[codigoConexao]", codigoConexao != null ? codigoConexao : "")
                .data("yt0", "Entrar")
                .method(Connection.Method.POST)
                .ignoreHttpErrors(true)
                .execute();

        if (authResponse.body().contains("Senha incorreta") || authResponse.body().contains("Incorrect username")) {
            throw new IOException("Credenciais do ERP inválidas. Verifique o application.properties.");
        }
    
        if (authResponse.url().toString().contains("/login") && !authResponse.body().contains("dashboard")) {
        }

        this.cookies = authResponse.cookies();
        log.info("Autenticado com sucesso! Base URL dinâmica: {}", this.dynamicBaseUrl);
    }

    private String safeStr(String value) {
        return value == null ? "" : value.trim();
    }
}