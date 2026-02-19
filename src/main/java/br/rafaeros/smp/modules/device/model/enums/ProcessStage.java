package br.rafaeros.smp.modules.device.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ProcessStage {
    PANEL_ASSEMBLY("Montagem de Painel"),
    BENCH_ASSEMBLY("Montagem de Bancada"),
    SOLDERING("Soldagem"),
    QUALITY_CONTROL("Controle de Qualidade"),
    PACKAGING("Embalagem"),
    TESTING("Teste");

    private final String description;

    ProcessStage(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

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