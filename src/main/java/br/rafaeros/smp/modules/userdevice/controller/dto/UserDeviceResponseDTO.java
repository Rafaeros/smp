package br.rafaeros.smp.modules.userdevice.controller.dto;

import br.rafaeros.smp.modules.device.model.enums.DeviceStatus;
import br.rafaeros.smp.modules.device.model.enums.ProcessStatus;
import br.rafaeros.smp.modules.userdevice.model.UserDevice; // Importe sua entidade de relação

public record UserDeviceResponseDTO (
    Long deviceId,
    String name,           // Nome customizado (ex: "Solda 01")
    String macAddress,
    String ipAddress,
    DeviceStatus status,
    ProcessStatus process,
    String lastSeen,
    Double coordinateX,
    Double coordinateY
) {
    public static UserDeviceResponseDTO fromEntity(UserDevice userDevice) {
        return new UserDeviceResponseDTO(
            userDevice.getDevice().getId(),
            userDevice.getName(),
            userDevice.getDevice().getMacAddress(),
            userDevice.getDevice().getIpAddress(),
            userDevice.getDevice().getStatus(),
            userDevice.getDevice().getProcessStatus(),
            userDevice.getDevice().getLastSeen() != null ? userDevice.getDevice().getLastSeen().toString() : null,
            userDevice.getCoordinateX(),
            userDevice.getCoordinateY()
        );
    }
}