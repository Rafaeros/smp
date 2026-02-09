package br.rafaeros.smp.modules.device.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ProcessStage {
    PANEL_ASSEMBLY,
    BENCH_ASSEMBLY,
    SOLDERING,
    QUALITY_CONTROL,
    PACKAGING,
    TESTING;

@JsonCreator
    public static ProcessStage fromString(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return ProcessStage.valueOf(value.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estágio inválido. Use: " + java.util.Arrays.toString(values()));
        }
    }
}