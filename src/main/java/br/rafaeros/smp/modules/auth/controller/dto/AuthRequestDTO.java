package br.rafaeros.smp.modules.auth.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthRequestDTO (
    @NotBlank(message = "O username é obrigatório")
    String username,

    @NotBlank(message = "A senha é obrigatória")
    String password
) {}