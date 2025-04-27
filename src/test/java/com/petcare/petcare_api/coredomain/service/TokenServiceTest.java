package com.petcare.petcare_api.coredomain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {

    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "secret", "my-test-secret");
    }

    @Test
    void shouldGenerateAndValidateToken() throws Exception {
        String email = "user@example.com";
        String token = tokenService.generateToken(email);

        assertNotNull(token);
        String validatedEmail = tokenService.validateToken(token);
        assertEquals(email, validatedEmail);
    }

    @Test
    void shouldThrowExceptionWhenTokenIsInvalid() {
        Exception exception = assertThrows(Exception.class, () -> {
            tokenService.validateToken("invalid.token.value");
        });

        assertEquals("Token inválido ou expirado", exception.getMessage());
    }
}
