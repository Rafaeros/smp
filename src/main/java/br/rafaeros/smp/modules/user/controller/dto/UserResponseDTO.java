package br.rafaeros.smp.modules.user.controller.dto;

import br.rafaeros.smp.modules.user.model.User;

public record UserResponseDTO(
        Long id,
        String firstName,
        String lastName,
        String username,
        String email,
        String role
    ) {
    public static UserResponseDTO fromEntity(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name());
    }
}