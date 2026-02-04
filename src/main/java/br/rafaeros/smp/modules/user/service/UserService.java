package br.rafaeros.smp.modules.user.service;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.rafaeros.smp.core.exception.BussinessException;
import br.rafaeros.smp.core.exception.ResourceNotFoundException;
import br.rafaeros.smp.modules.user.controller.dto.CreateUserRequestDTO;
import br.rafaeros.smp.modules.user.controller.dto.UserResponseDTO;
import br.rafaeros.smp.modules.user.model.User;
import br.rafaeros.smp.modules.user.model.enums.Role;
import br.rafaeros.smp.modules.user.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private final String DEFAULT_PASSWORD = "mudar@123";

    @Transactional
    public UserResponseDTO createUser(CreateUserRequestDTO dto) {

        if (userRepository.existsByUsername(dto.username())) {
            throw new BussinessException("Nome de usuario ja cadastrado");
        }

        Role userRole;
        try {
            userRole = Role.valueOf(dto.role());
        } catch (IllegalArgumentException e) {
            throw new BussinessException("Cargo não encontrado");
        }

        User user = new User();
        user.setUsername(dto.username());
        user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        user.setRole(userRole);
        User savedUser = userRepository.save(user);

        return UserResponseDTO.fromEntity(savedUser);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> findAll() {
        return userRepository.findAll()
                .stream()
                .map(UserResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findById(Long id) {
        User user = findByIdInternal(id);
        return UserResponseDTO.fromEntity(user);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário nao encontrado"));
        return UserResponseDTO.fromEntity(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
    }

    @Transactional
    public UserResponseDTO update(Long id, CreateUserRequestDTO dto) {
        User user = findByIdInternal(id);
        user.setUsername(dto.username());
        user.setRole(Role.valueOf(dto.role()));
        User savedUser = userRepository.save(user);
        return UserResponseDTO.fromEntity(savedUser);
    }

    private User findByIdInternal(Long id) {
        Long safeId = Objects.requireNonNull(id);
        return userRepository.findById(safeId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    }

    @Transactional
    public void delete(Long id) {
        User user = findByIdInternal(id);

        if (user == null) {
            throw new ResourceNotFoundException("Usuário nao encontrado");
        }

        userRepository.delete(user);
    }

}
