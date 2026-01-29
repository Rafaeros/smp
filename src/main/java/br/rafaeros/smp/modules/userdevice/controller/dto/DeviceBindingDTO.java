package br.rafaeros.smp.modules.userdevice.controller.dto;

public record DeviceBindingDTO (
    Long id,
    String name,
    String macAddress,
    String ipAddress,
    Double coordinateX,
    Double coordinateY
) {}
