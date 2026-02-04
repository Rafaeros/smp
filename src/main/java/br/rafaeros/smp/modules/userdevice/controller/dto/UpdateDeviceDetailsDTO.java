package br.rafaeros.smp.modules.userdevice.controller.dto;

public record UpdateDeviceDetailsDTO (
    Long id,
    String name,
    String processStage,
    String order
) {}
