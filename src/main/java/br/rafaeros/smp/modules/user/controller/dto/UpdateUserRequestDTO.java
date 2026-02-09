package br.rafaeros.smp.modules.user.controller.dto;

public record UpdateUserRequestDTO (
    String firstName,
    String lastName,
    String email,
    String username,
    String role
) {}
