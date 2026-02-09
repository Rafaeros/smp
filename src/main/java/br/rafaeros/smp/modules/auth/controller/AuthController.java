package br.rafaeros.smp.modules.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.rafaeros.smp.core.dto.ApiResponse;
import br.rafaeros.smp.modules.auth.controller.dto.AuthRequestDTO;
import br.rafaeros.smp.modules.auth.controller.dto.AuthResponseDTO;
import br.rafaeros.smp.modules.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> login(@RequestBody @Valid AuthRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.success("Login realizado com sucesso!", authService.login(dto)));
    }
}
