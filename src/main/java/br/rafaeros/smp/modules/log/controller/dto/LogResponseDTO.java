package br.rafaeros.smp.modules.log.controller.dto;

import java.time.Instant;

import br.rafaeros.smp.modules.log.model.Log;

public record LogResponseDTO(
        Long id,
        Instant createdAt,
        Double cycleTime,
        Double pausedTime,
        Double totalTime,
        Long quantityProduced,
        String macAddress,
        String code,
        String productCode) {
    public static LogResponseDTO fromEntity(Log log) {
        return new LogResponseDTO(
                log.getId(),
                log.getCreatedAt(),
                log.getCycleTime(),
                log.getPausedTime(),
                log.getTotalTime(),
                log.getQuantityProduced(),
                log.getDevice() != null ? log.getDevice().getMacAddress() : "N/A",
                log.getOrder().getCode(),
                log.getOrder().getProduct().getCode());
    }
}