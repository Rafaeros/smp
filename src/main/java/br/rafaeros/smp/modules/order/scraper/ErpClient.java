package br.rafaeros.smp.modules.order.scraper;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

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

    public Document searchOrders(ErpSearchFilter filter) {
        try {
            authenticate();
            String searchUrl = this.dynamicBaseUrl + "/ordemProducao/exportarOrdens";

            log.info("Filter: {}", filter.toString());

            return Jsoup.connect(searchUrl)
                    .cookies(cookies)
                    .userAgent("Mozilla/5.0")
                    .timeout(120000)
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

        } catch (IOException e) {
            log.error("Erro na busca. Tentando renovar sessão...", e);
            this.cookies = null;
            this.dynamicBaseUrl = null;
            try {
                authenticate();
                return searchOrders(filter);
            } catch (IOException ex) {
                throw new RuntimeException("Falha fatal ao conectar no ERP.", ex);
            }
        }
    }

    private void authenticate() throws IOException {
        if (cookies != null && dynamicBaseUrl != null) return;
        Connection.Response loginPageResponse = Jsoup.connect(properties.getUrl())
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8")
                .followRedirects(true)
                .method(Connection.Method.GET)
                .execute();

        URL finalUrl = loginPageResponse.url();
        String loginUrlString = finalUrl.toString();
        this.dynamicBaseUrl = finalUrl.getProtocol() + "://" + finalUrl.getHost();

        Document loginDoc = loginPageResponse.parse();
        String csrfToken = loginDoc.select("input[name=YII_CSRF_TOKEN]").val();
        String codigoConexao = loginDoc.select("input[name='LoginForm[codigoConexao]']").val();

        if (codigoConexao == null || codigoConexao.isEmpty()) {
            if (loginUrlString.contains("?c=")) {
                codigoConexao = loginUrlString.split("\\?c=")[1].split("&")[0];
                codigoConexao = java.net.URLDecoder.decode(codigoConexao, java.nio.charset.StandardCharsets.UTF_8);
            }
        }
        
        if (csrfToken == null) csrfToken = "";


        Connection.Response authResponse = Jsoup.connect(loginUrlString)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .cookies(loginPageResponse.cookies())
                .header("Origin", this.dynamicBaseUrl)
                .header("Referer", loginUrlString)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .data("YII_CSRF_TOKEN", csrfToken)
                .data("LoginForm[username]", properties.getUsername())
                .data("LoginForm[password]", properties.getPassword())
                .data("LoginForm[codigoConexao]", codigoConexao)
                .data("yt0", "Entrar")
                .method(Connection.Method.POST)
                .ignoreHttpErrors(true)
                .execute();

        if (authResponse.body().contains("Senha incorreta") || authResponse.body().contains("Incorrect username")) {
            throw new IOException("Credenciais inválidas.");
        }
        
        if (authResponse.url().toString().contains("/login") && !authResponse.body().contains("dashboard")) {
            throw new IOException("Credenciais inválidas.");
        }

        this.cookies = authResponse.cookies();
        log.info("Autenticado com sucesso em: {}", this.dynamicBaseUrl);
    }

    private String safeStr(String value) {
        return value == null ? "" : value.trim();
    }
}
