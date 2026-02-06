package br.rafaeros.smp.modules.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import br.rafaeros.smp.core.security.TokenService;
import br.rafaeros.smp.modules.auth.controller.dto.AuthRequestDTO;
import br.rafaeros.smp.modules.auth.controller.dto.AuthResponseDTO;
import br.rafaeros.smp.modules.user.model.User;

@Service
public class AuthService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    public AuthResponseDTO login(AuthRequestDTO dto) {
        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(dto.username(), dto.password());
            var authentication = authenticationManager.authenticate(usernamePassword);
            
            User user = (User) authentication.getPrincipal();
            String token = tokenService.generateToken(user);
            
            return new AuthResponseDTO(token);

        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Credenciais inválidas"); 
        }
    }
}