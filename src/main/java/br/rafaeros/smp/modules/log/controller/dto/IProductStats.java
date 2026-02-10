package br.rafaeros.smp.modules.log.controller.dto;

public interface IProductStats {
    String getCode();
    String getDescription();
    
    Long getTotalLogs();
    Double getAvgCycleTime();
    Double getMinCycleTime();
    Double getMaxCycleTime();
}