package br.rafaeros.smp.modules.userdevice.controller.dto;

import br.rafaeros.smp.modules.device.model.enums.DeviceStatus;
import br.rafaeros.smp.modules.device.model.enums.ProcessStatus;

public record UserDeviceResponseDTO (
    String name,
    String macAddress,
    String ipAddress,
    DeviceStatus status,
    ProcessStatus process,
    String lastSeen
) {}
