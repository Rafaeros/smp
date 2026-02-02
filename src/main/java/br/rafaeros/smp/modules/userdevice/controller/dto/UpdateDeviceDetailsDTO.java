package br.rafaeros.smp.modules.userdevice.controller.dto;

import br.rafaeros.smp.modules.device.model.enums.ProcessStage;

public record UpdateDeviceDetailsDTO (
    Long id,
    String name,
    ProcessStage processStage,
    String order
) {}
