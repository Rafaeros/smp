package br.rafaeros.smp.modules.order.scraper;

import java.time.Instant;

public record OrderScrapeDTO(
    String code,
    String clientName,
    String productCode,
    String productDescription,
    Integer totalQuantity,
    Integer producedQuantity,
    String status,
    Instant creationDate,
    Instant deliveryDate 
) {}