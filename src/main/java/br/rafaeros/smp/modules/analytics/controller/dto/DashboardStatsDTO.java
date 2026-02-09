package br.rafaeros.smp.modules.analytics.controller.dto;

public record DashboardStatsDTO(
    Long totalProduced,
    Double avgCycleTime,
    Double minCycleTime,
    Double maxCycleTime,
    Double efficiency
) {}