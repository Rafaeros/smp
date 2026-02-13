package br.rafaeros.smp.modules.user.service;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.rafaeros.smp.core.exception.BusinessException;
import br.rafaeros.smp.core.exception.ResourceNotFoundException;
import br.rafaeros.smp.modules.user.controller.dto.CreateUserRequestDTO;
import br.rafaeros.smp.modules.user.controller.dto.UpdatePasswordRequestDTO;
import br.rafaeros.smp.modules.user.controller.dto.UpdateUserRequestDTO;
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
            throw new BusinessException("Nome de usuário ja cadastrado");
        }

        Role userRole;
        try {
            userRole = Role.valueOf(dto.role());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Cargo não encontrado");
        }

        User user = new User();
        user.setFirstName(dto.firstName());
        user.setLastName(dto.lastName());
        user.setEmail(dto.email());
        user.setUsername(dto.username());
        user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        user.setRole(userRole);
        User savedUser = userRepository.save(user);

        return UserResponseDTO.fromEntity(savedUser);
    }

    @Transactional(readOnly = true)
    public Page<UserResponseDTO> findAll(Pageable pageable, String firstName, String lastName, String username,
            String email) {
        Pageable safePage = Objects.requireNonNull(pageable);

        if (firstName == null && lastName == null && username == null && email == null) {
            return userRepository.findAll(safePage).map(UserResponseDTO::fromEntity);
        }

        String firstNameFilter = (firstName != null && !firstName.isBlank()) ? "%" + firstName + "%" : null;
        String lastNameFilter = (lastName != null && !lastName.isBlank()) ? "%" + lastName + "%" : null;
        String usernameFilter = (username != null && !username.isBlank()) ? "%" + username + "%" : null;
        String emailFilter = (email != null && !email.isBlank()) ? "%" + email + "%" : null;

        return userRepository.findByFilters(safePage, firstNameFilter, lastNameFilter, usernameFilter, emailFilter)
                .map(UserResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findById(Long id) {
        User user = findByIdInternal(id);
        return UserResponseDTO.fromEntity(user);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        return UserResponseDTO.fromEntity(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
    }

    @Transactional
    public UserResponseDTO update(Long id, UpdateUserRequestDTO dto) {
        User user = findByIdInternal(id);

        if (user == null) {
            throw new ResourceNotFoundException("Usuário não encontrado com ID:" + id);
        }

        if (dto.firstName() != null) {
            user.setFirstName(dto.firstName());
        }

        if (dto.lastName() != null) {
            user.setLastName(dto.lastName());
        }

        if (dto.username() != null) {
            if (userRepository.existsByUsername(dto.username())) {
                throw new BusinessException("Nome de usuário já cadastrado.");
            }
            user.setUsername(dto.username());
        }

        if (dto.email() != null) {
            user.setEmail(dto.email());
        }

        if (dto.role() != null) {
            Role userRole;
            try {
                userRole = Role.valueOf(dto.role());
            } catch (IllegalArgumentException e) {
                throw new BusinessException("Cargo não encontrado");
            }
            user.setRole(userRole);
        }

        User savedUser = userRepository.save(user);
        return UserResponseDTO.fromEntity(savedUser);
    }

    public UserResponseDTO changePassword(Long id, UpdatePasswordRequestDTO dto, User user) {
        if (!passwordEncoder.matches(dto.currentPassword(), user.getPassword())) {
            throw new BusinessException("Senha atual incorreta");
        }

        if (!dto.newPassword().equals(dto.confirmNewPassword())) {
            throw new BusinessException("As senhas novas devem ser iguais");
        }

        if (!dto.newPassword().equals(dto.currentPassword())) {
            user.setPassword(passwordEncoder.encode(dto.newPassword()));
            User savedUser = userRepository.save(user);
            return UserResponseDTO.fromEntity(savedUser);
        } else {
            throw new BusinessException("A nova senha deve ser diferente da senha atual");
        }

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
            throw new ResourceNotFoundException("Usuário não encontrado");
        }

        userRepository.delete(user);
    }

}
