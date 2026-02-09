package br.rafaeros.smp.modules.userdevice.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DeviceBindingDTO(
        @NotNull(message = "O ID do dispositivo deve ser informado") Long id,

        @NotBlank(message = "O nome deve ser informado") String name,

        @NotBlank(message = "O MAC Address deve ser informado") String macAddress,

        @NotBlank(message = "O IP Address deve ser informado") String ipAddress,

        @NotNull(message = "A coordenada X deve ser informada") Double coordinateX,

        @NotNull(message = "A coordenada Y deve ser informada") Double coordinateY) {
}
