package br.rafaeros.smp.modules.device.controller.dto;

public record UpdateDeviceDTO (
    String macAddress,
    String ipAddress
) {}
