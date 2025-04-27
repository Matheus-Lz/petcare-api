package com.petcare.petcare_api.utils;

import com.petcare.petcare_api.application.dto.user.*;
import com.petcare.petcare_api.coredomain.model.User;
import com.petcare.petcare_api.infrastructure.enums.user.UserRole;

public class UserTestFactory {

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

    public static User buildUser(UserRole role) {
        return User.builder()
                .email(DEFAULT_EMAIL)
                .password("encodedpassword")
                .cpfCnpj(DEFAULT_CPFCNPJ)
                .name(DEFAULT_NAME)
                .role(role)
                .build();
    }

    public static User buildUserWithId(String id, UserRole role) {
        User user = buildUser(role);
        user.setId(id);
        return user;
    }
}
