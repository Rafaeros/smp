package br.rafaeros.smp.modules.order.controller.dto;

public record OrderExportDTO(
    String orderCode,
    String productCode,
    String clientName,
    String status,
    Integer totalQuantity,
    Integer producedQuantity,
    Long totalLogs,
    Double minTime,
    Double avgTime,
    Double maxTime
) {}