package br.rafaeros.smp.modules.log.controller.dto;

public record DeviceLogPayloadDTO(
    Long orderId,
    Double cycleTime,
    Double pausedTime,   
    Long quantityProduced,
    Long quantityPaused  
) {}