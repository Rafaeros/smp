package br.rafaeros.smp.modules.orderlog.controller.dto;

public interface IOrderStats {
    Long getTotalLogs();
    Double getAvgCycleTime();
    Double getMinCycleTime();
    Double getMaxCycleTime();
    Long getQuantityProduced();
}