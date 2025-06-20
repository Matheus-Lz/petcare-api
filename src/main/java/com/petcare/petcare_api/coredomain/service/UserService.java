package com.petcare.petcare_api.coredomain.service;

import com.petcare.petcare_api.application.dto.user.*;
import com.petcare.petcare_api.coredomain.model.user.User;
import com.petcare.petcare_api.coredomain.model.user.enums.UserRole;
import com.petcare.petcare_api.infrastructure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final TokenService tokenService;
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(TokenService tokenService, UserRepository repository, PasswordEncoder passwordEncoder) {
        this.tokenService = tokenService;
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO requestDTO, AuthenticationManager authenticationManager) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDTO.email(), requestDTO.password())
        );
        User authUser = (User) auth.getPrincipal();
        String token = tokenService.generateToken(authUser.getEmail());
        return new AuthenticationResponseDTO(token, authUser.getRole().name(), authUser.getName(), authUser.getId());
    }

    public void registerUser(RegisterRequestDTO requestDTO) {
        this.registerUser(requestDTO, UserRole.USER);
    }

    public User registerUser(RegisterRequestDTO requestDTO, UserRole role) {
        if (repository.findByEmail(requestDTO.email()) != null) {
            throw new IllegalArgumentException("Email já em uso");
        }

        User newUser = User.builder()
                .email(requestDTO.email())
                .password(new BCryptPasswordEncoder().encode(requestDTO.password()))
                .name(requestDTO.name())
                .cpfCnpj(requestDTO.cpfCnpj())
                .role(role)
                .build();
        newUser.validate();

        return repository.save(newUser);
    }

    public void updateUser(String id, UpdateUserRequestDTO dto) {
        User user = this.getById(id);

        if (!Objects.equals(user.getId(), getCurrentUser().getId())) {
            throw new IllegalArgumentException("Não é possível alterar outro usuário");
        }

        if (dto.email() != null && !dto.email().isBlank()) {
            user.setEmail(dto.email());
        }

        if (dto.name() != null && !dto.name().isBlank()) {
            user.setName(dto.name());
        }

        if (dto.cpfCnpj() != null && !dto.cpfCnpj().isBlank()) {
            user.setCpfCnpj(dto.cpfCnpj());
        }

        if (dto.password() != null && !dto.password().isBlank()) {
            if (dto.currentPassword() == null || dto.currentPassword().isBlank()) {
                throw new RuntimeException("Senha atual é obrigatória para alterar a senha");
            }

            if (!passwordEncoder.matches(dto.currentPassword(), user.getPassword())) {
                throw new RuntimeException("Senha atual incorreta");
            }

            user.setPassword(passwordEncoder.encode(dto.password()));
        }

        repository.save(user);
    }


    public User getById(String userId) {
        Optional<User> optionalUser = repository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("Usuário não encontrado");
        }

        return optionalUser.get();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user = repository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("Usuário não encontrado");
        }
        return user;
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() == "anonymousUser") return null;

        return (User) authentication.getPrincipal();
    }
}
