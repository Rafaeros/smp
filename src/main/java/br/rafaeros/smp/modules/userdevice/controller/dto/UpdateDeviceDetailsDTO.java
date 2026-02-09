package br.rafaeros.smp.modules.userdevice.controller.dto;

import br.rafaeros.smp.modules.device.model.enums.ProcessStage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateDeviceDetailsDTO(
        @NotBlank(message = "O nome deve ser informado") String name,

        @NotNull(message = "O estágio do processo deve ser informado") ProcessStage processStage,

        @NotNull(message = "O ID da OP deve ser informada") Long orderId) {
}
