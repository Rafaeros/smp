package br.rafaeros.smp.modules.product.controller.dto;

public record ProductExportDTO(
    String productCode,
    String productDescription,
    Long totalLogs,
    Double avgTime,
    Double minTime,
    Double maxTime
) {}