package br.rafaeros.smp.modules.userdevice.controller.dto;

import br.rafaeros.smp.modules.device.model.enums.DeviceStatus;
import br.rafaeros.smp.modules.device.model.enums.ProcessStage;
import br.rafaeros.smp.modules.device.model.enums.ProcessStatus;
import br.rafaeros.smp.modules.userdevice.model.UserDevice;

public record UserDeviceDetailsDTO(
        Long userDeviceId,
        String name,
        String macAddress,
        String ipAddress,
        DeviceStatus status,
        ProcessStatus process,
        ProcessStage stage,
        String lastSeen,
        Double coordinateX,
        Double coordinateY
    ) {
    public static UserDeviceDetailsDTO fromEntity(UserDevice userDevice) {
        return new UserDeviceDetailsDTO(
                userDevice.getId(),
                userDevice.getName(),
                userDevice.getDevice().getMacAddress(),
                userDevice.getDevice().getIpAddress(),
                userDevice.getDevice().getStatus(),
                userDevice.getDevice().getProcessStatus(),
                userDevice.getDevice().getCurrentStage(),
                userDevice.getDevice().getLastSeen().toString(),
                userDevice.getCoordinateX(),
                userDevice.getCoordinateY());
    }
}
