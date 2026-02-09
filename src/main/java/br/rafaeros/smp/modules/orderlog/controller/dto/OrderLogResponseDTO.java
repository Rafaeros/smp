package br.rafaeros.smp.modules.orderlog.controller.dto;

import java.time.Instant;

import br.rafaeros.smp.modules.orderlog.model.OrderLog;

public record OrderLogResponseDTO(
    Long id,
    Instant createdAt,
    Double cycleTime,
    Long quantityProduced,
    String deviceMac,
    String orderCode,
    String productName 
) {
    // Construtor estático para facilitar a conversão (Mapper manual)
    public static OrderLogResponseDTO fromEntity(OrderLog log) {
        return new OrderLogResponseDTO(
            log.getId(),
            log.getCreatedAt(),
            log.getCycleTime(),
            log.getQuantityProduced(),
            log.getDevice() != null ? log.getDevice().getMacAddress() : "N/A",
            log.getOrder().getCode(),
            log.getOrder().getProduct().getCode()
        );
    }
}