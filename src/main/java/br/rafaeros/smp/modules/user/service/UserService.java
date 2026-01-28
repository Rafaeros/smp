package br.rafaeros.smp.modules.user.service;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.rafaeros.smp.core.exception.BussinessException;
import br.rafaeros.smp.core.exception.ResourceNotFoundException;
import br.rafaeros.smp.modules.user.controller.dto.CreateUserRequestDTO;
import br.rafaeros.smp.modules.user.controller.dto.UserResponseDTO;
import br.rafaeros.smp.modules.user.model.User;
import br.rafaeros.smp.modules.user.repository.UserRepository;

@Service
public class UserService {
    @Autowired private UserRepository userRepository;
    

    @Transactional
    private UserResponseDTO registerUser(CreateUserRequestDTO dto) {
        boolean exists = userRepository.existsByUsername(dto.username());
        if (exists) {
            throw new BussinessException("Username ");
        }

        User user = new User();
        user.setUsername(dto.username());
        user.setPassword(dto.password());
        User savedUser = userRepository.save(user);
        
        return UserResponseDTO.fromEntity(savedUser);
    }

    @Transactional(readOnly = true)
    private UserResponseDTO findById(Long id) {
        Long safeId = Objects.requireNonNull(id);
        User user = userRepository.findById(safeId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário nao encontrado"));
        return UserResponseDTO.fromEntity(user);
    }

    @Transactional(readOnly = true)
    private UserResponseDTO findByUsername(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário nao encontrado"));
        return UserResponseDTO.fromEntity(user);
    }

}
