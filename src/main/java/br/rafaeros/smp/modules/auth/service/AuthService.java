package br.rafaeros.smp.modules.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import br.rafaeros.smp.core.security.TokenService;
import br.rafaeros.smp.modules.auth.controller.dto.AuthRequestDTO;
import br.rafaeros.smp.modules.auth.controller.dto.AuthResponseDTO;
import br.rafaeros.smp.modules.user.model.User;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthResponseDTO login(AuthRequestDTO dto) {
        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(dto.username(), dto.password());
            var authentication = authenticationManager.authenticate(usernamePassword);

            User user = (User) authentication.getPrincipal();
            String token = tokenService.generateToken(user);

            return new AuthResponseDTO(token, user.getId(), user.getUsername(), user.getRole().name());

        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Credenciais inválidas");
        }
    }
}