package com.petcare.petcare_api.application.dto.user;

public record AuthenticationResponseDTO(String token, String role, String name, String userId) {
}
