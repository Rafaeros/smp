package br.rafaeros.smp.modules.userdevice.controller.dto;

import br.rafaeros.smp.modules.device.model.enums.ProcessStage;

public record UpdateDeviceDetailsDTO(
        String name,
        ProcessStage processStage,
        Long orderId,
        Double coordinateX,
        Double coordinateY
) {}