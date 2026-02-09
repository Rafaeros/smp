package br.rafaeros.smp.modules.orderlog.controller.dto;

public record ProductStatsDTO(
    String name,
    Long totalLogs,
    Double avgCycleTime,
    Double minCycleTime,
    Double maxCycleTime
) {}