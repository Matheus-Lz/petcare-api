package com.petcare.petcare_api.coredomain.model.user.enums;

import lombok.Getter;

@Getter
public enum UserRole {

    USER("USER"),
    SUPER_ADMIN("SUPER_ADMIN"),
    EMPLOYEE("EMPLOYEE");

    private final String role;

    UserRole(String role) {
        this.role = role;
    }

}
