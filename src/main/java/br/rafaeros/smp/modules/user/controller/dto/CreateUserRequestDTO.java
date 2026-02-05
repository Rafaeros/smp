package br.rafaeros.smp.modules.user.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequestDTO(

    @NotBlank(message = "O nome é obrigatório")
    String firstName,

    @NotBlank(message = "O sobrenome é obrigatório")
    String lastName,

    @NotBlank(message = "O email é obrigatório")
    @Email
    String email,


    @NotBlank(message = "O nome de usuário é obrigatório")
    @Size(min = 3, max = 20, message = "O nome de usuário deve ter entre 3 e 20 caracteres")
    String username,

    @NotBlank(message = "O Cargo é obrigatório")
    String role
) {}

