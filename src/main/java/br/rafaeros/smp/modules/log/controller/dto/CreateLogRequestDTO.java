package br.rafaeros.smp.modules.log.controller.dto;

import jakarta.validation.constraints.NotNull;

public record CreateLogRequestDTO(
        @NotNull(message = "A quantidade produzida deve ser informada") Long quantityProduced,

        @NotNull(message = "A quantidade de pausas deve ser informada") Long quantityPaused,

        @NotNull(message = "O tempo de ciclo deve ser informado") Double cycleTime,

        @NotNull(message = "O tempo de pausa deve ser informado") Double pausedTime,

        @NotNull(message = "O id da ordem deve ser informado") Long orderId,

        @NotNull(message = "O id do dispositivo deve ser informado") Long deviceId) {
}
