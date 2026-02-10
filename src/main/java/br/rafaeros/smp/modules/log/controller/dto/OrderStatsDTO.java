package br.rafaeros.smp.modules.log.controller.dto;

public record OrderStatsDTO (
    Long totalLogs,
    Double avgCycleTime,
    Double minCycleTime,
    Double maxCycleTime,
    Long quantityProduced
) {}
