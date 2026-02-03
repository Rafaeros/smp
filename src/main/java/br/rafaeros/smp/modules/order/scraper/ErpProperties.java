package br.rafaeros.smp.modules.order.scraper;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "smp.integration.erp")
@Getter @Setter
public class ErpProperties {
    private String url;
    private String username;
    private String password;
    private int timeout;
}
