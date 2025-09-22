package com.petcare.petcare_api.utils;

import com.petcare.petcare_api.application.dto.user.*;
import com.petcare.petcare_api.coredomain.model.user.User;
import com.petcare.petcare_api.coredomain.model.user.enums.UserRole;
import com.petcare.petcare_api.infrastructure.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserTestFactory {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserTestFactory(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private static final String DEFAULT_EMAIL = "test@example.com";
    private static final String DEFAULT_PASSWORD = "123456";
    private static final String DEFAULT_CPFCNPJ = "23602088000117";
    private static final String DEFAULT_NAME = "Test User";

    private static final String UPDATED_EMAIL = "updated@example.com";
    private static final String UPDATED_PASSWORD = "654321";
    private static final String UPDATED_CPFCNPJ = "40846120000129";
    private static final String UPDATED_NAME = "Updated User";

    public static RegisterRequestDTO buildRegisterRequest() {
        return new RegisterRequestDTO(
                DEFAULT_EMAIL,
                DEFAULT_PASSWORD,
                DEFAULT_CPFCNPJ,
                DEFAULT_NAME
        );
    }

    public static UpdateUserRequestDTO buildUpdateRequest() {
        return new UpdateUserRequestDTO(
                UPDATED_EMAIL,
                DEFAULT_PASSWORD,
                UPDATED_PASSWORD,
                UPDATED_CPFCNPJ,
                UPDATED_NAME
        );
    }

    public static AuthenticationRequestDTO buildAuthRequest() {
        return new AuthenticationRequestDTO(
                DEFAULT_EMAIL,
                DEFAULT_PASSWORD
        );
    }

    public static User buildUser() {
        return buildUser(UserRole.USER);
    }

    public static User buildUser(UserRole role) {
        return User.builder()
                .email(DEFAULT_EMAIL)
                .password(DEFAULT_PASSWORD)
                .cpfCnpj(DEFAULT_CPFCNPJ)
                .name(DEFAULT_NAME)
                .role(role)
                .build();
    }

    public User persistUser(UserRole role, String email, String cpfCnpj) {
        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(DEFAULT_PASSWORD))
                .cpfCnpj(cpfCnpj)
                .name(DEFAULT_NAME)
                .role(role)
                .build();
        return userRepository.save(user);
    }
}