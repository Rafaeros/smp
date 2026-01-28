package br.rafaeros.smp.modules.user.controller.dto;

import br.rafaeros.smp.modules.user.model.User;

public record UserResponseDTO (
    Long id,
    String username
) {
    public static UserResponseDTO fromEntity(User user) {
        return new UserResponseDTO(
            user.getId(),
            user.getUsername()
        );
    }
}   