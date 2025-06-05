package com.petcare.petcare_api.coredomain.service;

import com.petcare.petcare_api.application.dto.user.*;
import com.petcare.petcare_api.coredomain.model.User;
import com.petcare.petcare_api.infrastructure.enums.user.UserRole;
import com.petcare.petcare_api.infrastructure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final TokenService tokenService;
    private final UserRepository repository;

    @Autowired
    public UserService(TokenService tokenService, UserRepository repository) {
        this.tokenService = tokenService;
        this.repository = repository;
    }

    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO requestDTO, AuthenticationManager authenticationManager) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDTO.email(), requestDTO.password())
        );
        User authUser = (User) auth.getPrincipal();
        String token = tokenService.generateToken(authUser.getEmail());
        return new AuthenticationResponseDTO(token, authUser.getRole().name(), authUser.getName());
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

    public Page<User> listUsers(Integer page, Integer size) {
        return repository.findAll(PageRequest.of(page, size));
    }

    public void updateUser(String userId, UpdateUserRequestDTO requestDTO) {
        User user = this.getById(userId);

        user.setEmail(requestDTO.email());
        user.setPassword(new BCryptPasswordEncoder().encode(requestDTO.password()));
        user.setName(requestDTO.name());
        user.setCpfCnpj(requestDTO.cpfCnpj());
        user.validate();

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
}
