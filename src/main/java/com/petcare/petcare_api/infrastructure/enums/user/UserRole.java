package com.petcare.petcare_api.infrastructure.enums.user;

import lombok.Getter;

@Getter
public enum UserRole {

    USER("user"),
    SUPER_ADMIN("super-admin"),
    ADMIN("admin");

    private final String role;

    UserRole(String role) {
        this.role = role;
    }

}
