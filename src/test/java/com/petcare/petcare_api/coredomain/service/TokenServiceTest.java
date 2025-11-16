package com.petcare.petcare_api.coredomain.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.petcare.petcare_api.infrastructure.exception.UserExceptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Method;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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

    @Test
    void shouldWrapJwtCreationExceptionIntoTokenGenerationException() {
        ReflectionTestUtils.setField(tokenService, "secret", "my-test-secret");

        Algorithm algorithmMock = mock(Algorithm.class);
        JWTCreator.Builder builder = mock(JWTCreator.Builder.class);

        try (MockedStatic<Algorithm> alg = mockStatic(Algorithm.class);
             MockedStatic<JWT> jwt = mockStatic(JWT.class)) {

            alg.when(() -> Algorithm.HMAC256("my-test-secret")).thenReturn(algorithmMock);
            jwt.when(JWT::create).thenReturn(builder);

            when(builder.withIssuer(anyString())).thenReturn(builder);
            when(builder.withSubject(anyString())).thenReturn(builder);
            when(builder.withExpiresAt(any(Instant.class))).thenReturn(builder);

            when(builder.sign(algorithmMock)).thenThrow(new JWTCreationException("fail", null));

            assertThrows(UserExceptions.TokenGenerationException.class,
                    () -> tokenService.generateToken("user@example.com"));
        }
    }

    @Test
    void shouldThrowInvalidTokenExceptionWhenExpiredToken() {
        String expiredToken = com.auth0.jwt.JWT.create()
                .withIssuer("petcare_api")
                .withSubject("user@example.com")
                .withExpiresAt(java.util.Date.from(
                        java.time.Instant.now().minusSeconds(60)))
                .sign(com.auth0.jwt.algorithms.Algorithm.HMAC256("my-test-secret"));

        ReflectionTestUtils.setField(tokenService, "secret", "my-test-secret");

        assertThrows(UserExceptions.InvalidTokenException.class,
                () -> tokenService.validateToken(expiredToken));
    }

    @Test
    void shouldThrowInvalidTokenExceptionWhenJwtVerificationFails() {
        TokenService service = new TokenService();
        ReflectionTestUtils.setField(service, "secret", "wrong-secret");

        String token = tokenService.generateToken("user@example.com");

        assertThrows(UserExceptions.InvalidTokenException.class,
                () -> service.validateToken(token));
    }
}
