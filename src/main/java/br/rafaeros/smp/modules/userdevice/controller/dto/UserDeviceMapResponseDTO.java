package br.rafaeros.smp.modules.userdevice.controller.dto;

import br.rafaeros.smp.modules.device.model.enums.DeviceStatus;

public record UserDeviceMapResponseDTO(
        Long id,
        String name,
        String macAddress,
        Double x,
        Double y,
        DeviceStatus status) {
    public static UserDeviceMapResponseDTO fromEntity(br.rafaeros.smp.modules.userdevice.model.UserDevice userDevice) {
        return new UserDeviceMapResponseDTO(
                userDevice.getId(),
                userDevice.getName(),
                userDevice.getDevice().getMacAddress(),
                userDevice.getCoordinateX(),
                userDevice.getCoordinateY(),
                userDevice.getDevice().getStatus());
    }
}
