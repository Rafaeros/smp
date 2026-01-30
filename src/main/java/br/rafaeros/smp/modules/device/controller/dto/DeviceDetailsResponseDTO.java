package br.rafaeros.smp.modules.device.controller.dto;

import br.rafaeros.smp.modules.device.model.Device;
import br.rafaeros.smp.modules.device.model.enums.DeviceStatus;
import br.rafaeros.smp.modules.device.model.enums.ProcessStage;
import br.rafaeros.smp.modules.device.model.enums.ProcessStatus;

public record DeviceDetailsResponseDTO (
    Long id,
    String macAddress,
    String ipAddress,
    DeviceStatus status,
    ProcessStage stage,
    ProcessStatus process,
    String lastSeen
) {
    public static DeviceDetailsResponseDTO fromEntity(Device device) {
        return new DeviceDetailsResponseDTO(
                device.getId(),
                device.getMacAddress(),
                device.getIpAddress(),
                device.getStatus(),
                device.getCurrentStage(),
                device.getProcessStatus(),
                device.getLastSeen().toString()
        );
    }

}
