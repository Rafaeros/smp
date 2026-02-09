package br.rafaeros.smp.modules.orderlog.controller.dto;

public interface IProductStats {
    String getCode();
    String getName();
    
    Long getTotalLogs();
    Double getAvgCycleTime();
    Double getMinCycleTime();
    Double getMaxCycleTime();
}