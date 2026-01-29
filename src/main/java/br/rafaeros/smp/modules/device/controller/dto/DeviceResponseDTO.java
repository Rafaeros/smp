package br.rafaeros.smp.modules.device.controller.dto;

import br.rafaeros.smp.modules.device.model.Device;

public record DeviceResponseDTO(
        Long id,
        String macAddress,
        String ipAddress
    ) {
    public static DeviceResponseDTO fromEntity(Device device) {
        return new DeviceResponseDTO(device.getId(), device.getMacAddress(), device.getIpAddress());
    }
}
