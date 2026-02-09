package br.rafaeros.smp.modules.auth.controller.dto;

public record AuthResponseDTO(
    String token,
    Long id,
    String username,
    String role
) {}
