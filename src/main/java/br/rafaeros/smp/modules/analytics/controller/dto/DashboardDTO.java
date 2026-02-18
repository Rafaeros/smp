package br.rafaeros.smp.modules.analytics.controller.dto;

import java.util.List;

public class DashboardDTO {
    public record DashboardStatsDTO(
        Long totalProduced,
        Double avgCycleTime,
        Double minCycleTime,
        Double maxCycleTime,
        Double efficiency
    ) {}

    public record DashboardSummaryDTO(
        List<DashboardKPIDTO> kpis,
        List<DashboardLogDTO> recentLogs
    ) {}

    public record DashboardKPIDTO(
        String label,
        String value,
        String trend,
        String trendDirection, 
        String type
    ) {}

    public record DashboardLogDTO(
        Long id,
        String device,
        String action,
        String timestamp,
        String type
    ) {}
}