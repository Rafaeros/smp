package br.rafaeros.smp.modules.device.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateDeviceRequestDTO (
    @NotBlank(message = "O MAC Address é obrigatório")
    String macAddress,

    @NotBlank(message = "O IP Address é obrigatório")
    String ipAddress
) {}
