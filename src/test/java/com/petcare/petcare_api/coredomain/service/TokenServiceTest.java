package com.petcare.petcare_api.coredomain.service;

import com.petcare.petcare_api.infrastructure.exception.UserExceptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Method;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {

    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "secret", "my-test-secret");
    }

    @Test
    void shouldGenerateAndValidateToken() {
        String email = "user@example.com";
        String token = tokenService.generateToken(email);

        assertNotNull(token);

        String validatedEmail = tokenService.validateToken(token);
        assertEquals(email, validatedEmail);
    }

    @Test
    void shouldThrowExceptionWhenTokenIsInvalid() {
        Exception exception = assertThrows(UserExceptions.InvalidTokenException.class,
                () -> tokenService.validateToken("invalid.token.value"));

        assertEquals("Token inválido ou expirado", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenSecretIsInvalid() {
        ReflectionTestUtils.setField(tokenService, "secret", null);

        assertThrows(IllegalArgumentException.class,
                () -> tokenService.generateToken("user@example.com"));
    }


    @Test
    void shouldGenerateExpirationDateInFuture() throws Exception {
        Method method = TokenService.class.getDeclaredMethod("generateExpirationDate");
        method.setAccessible(true);

        Instant expiration = (Instant) method.invoke(tokenService);
        Instant now = LocalDateTime.now().toInstant(ZoneOffset.of("-03:00"));

        assertTrue(expiration.isAfter(now));
    }
}
