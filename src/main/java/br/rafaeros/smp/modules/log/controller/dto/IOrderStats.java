package br.rafaeros.smp.modules.log.controller.dto;

public interface IOrderStats {
    Long getTotalLogs();
    Double getAvgCycleTime();
    Double getMinCycleTime();
    Double getMaxCycleTime();
    Long getQuantityProduced();
}