package br.rafaeros.smp.modules.client.controller.dto;

import br.rafaeros.smp.modules.client.model.Client;

public record ClientResponseDTO(
        Long id,
        String name) {
    public static ClientResponseDTO fromEntity(Client client) {
        return new ClientResponseDTO(
                client.getId(),
                client.getName());
    }
}
