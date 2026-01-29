package br.rafaeros.smp.modules.device.controller.dto;

import br.rafaeros.smp.modules.device.model.Device;
import br.rafaeros.smp.modules.device.model.enums.DeviceStatus;
import br.rafaeros.smp.modules.device.model.enums.ProcessStatus;

public record UserDeviceResponseDTO (
    String name,
    String macAddress,
    String ipAddress,
    DeviceStatus status,
    ProcessStatus process,
    String lastSeen
) {
    public static UserDeviceResponseDTO fromEntity(Device device, String customName) {
        return new UserDeviceResponseDTO(
            customName,
            device.getMacAddress(),
            device.getIpAddress(),
            device.getStatus(),
            device.getProcessStatus(),
            device.getLastSeen() != null ? device.getLastSeen().toString() : null
        );
    }
}