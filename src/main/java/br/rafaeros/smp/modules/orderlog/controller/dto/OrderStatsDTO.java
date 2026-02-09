package br.rafaeros.smp.modules.orderlog.controller.dto;

public record OrderStatsDTO (
    Long totalLogs,
    Double avgCycleTime,
    Double minCycleTime,
    Double maxCycleTime,
    Long quantityProduced
) {}
