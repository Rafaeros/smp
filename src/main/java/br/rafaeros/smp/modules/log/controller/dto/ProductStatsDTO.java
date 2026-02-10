package br.rafaeros.smp.modules.log.controller.dto;

public record ProductStatsDTO(
    String code,
    String description,
    Long totalLogs,
    Double avgCycleTime,
    Double minCycleTime,
    Double maxCycleTime
) {
    public static ProductStatsDTO fromInterface(IProductStats stats) {
        if (stats == null) return null;
        return new ProductStatsDTO(
            stats.getCode(), 
            stats.getDescription(), 
            stats.getTotalLogs(), 
            stats.getAvgCycleTime(), 
            stats.getMinCycleTime(), 
            stats.getMaxCycleTime()
        );
    }
}