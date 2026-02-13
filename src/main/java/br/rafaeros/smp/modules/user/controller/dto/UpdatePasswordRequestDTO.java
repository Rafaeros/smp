package br.rafaeros.smp.modules.user.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdatePasswordRequestDTO (
    @NotBlank(message = "A senha atual deve ser informada")
    String currentPassword,

    @NotBlank(message = "A nova senha deve ser informada")
    @Size(min = 6, message = "A nova senha deve ter pelo menos 6 caracteres")
    String newPassword,

    @NotBlank(message = "A confirmação da nova senha deve ser informada")
    @Size(min = 6, message = "A nova senha deve ter pelo menos 6 caracteres")
    String confirmNewPassword
) {}