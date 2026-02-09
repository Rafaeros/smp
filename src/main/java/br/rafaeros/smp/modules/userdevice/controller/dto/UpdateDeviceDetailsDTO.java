package br.rafaeros.smp.modules.userdevice.controller.dto;

import br.rafaeros.smp.modules.device.model.enums.ProcessStage;
import jakarta.validation.constraints.NotBlank;

public record UpdateDeviceDetailsDTO (
    @NotBlank(message = "O ID do dispositivo deve ser informado")
    Long id,

    @NotBlank(message = "O nome deve ser informado")
    String name,

    @NotBlank(message = "O estágio do processo deve ser informado")
    ProcessStage processStage,

    @NotBlank(message = "A OP deve ser informada")
    String order
) {}
