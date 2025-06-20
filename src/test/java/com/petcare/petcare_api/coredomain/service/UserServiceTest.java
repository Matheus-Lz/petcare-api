package com.petcare.petcare_api.coredomain.service;

import com.petcare.petcare_api.application.dto.user.AuthenticationRequestDTO;
import com.petcare.petcare_api.application.dto.user.RegisterRequestDTO;
import com.petcare.petcare_api.application.dto.user.UpdateUserRequestDTO;
import com.petcare.petcare_api.coredomain.model.user.User;
import com.petcare.petcare_api.coredomain.model.user.enums.UserRole;
import com.petcare.petcare_api.infrastructure.repository.UserRepository;
import com.petcare.petcare_api.utils.UserTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DataJpaTest
@Import({UserService.class, TokenService.class, BCryptPasswordEncoder.class})
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private AuthenticationManager authenticationManager;

    @BeforeEach
    void setup() {
        authenticationManager = mock(AuthenticationManager.class);
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        RegisterRequestDTO request = UserTestFactory.buildRegisterRequest();

        var user = userService.registerUser(request, UserRole.USER);

        assertNotNull(user.getId());
        assertEquals(request.email(), user.getEmail());
        assertEquals(request.name(), user.getName());
    }

    @Test
    void shouldNotAllowDuplicateEmail() {
        RegisterRequestDTO request = UserTestFactory.buildRegisterRequest();

        userService.registerUser(request, UserRole.USER);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.registerUser(request, UserRole.USER)
        );

        assertEquals("Email já em uso", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        String fakeId = "00000000-0000-0000-0000-000000000000";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.getById(fakeId));

        assertEquals("Usuário não encontrado", exception.getMessage());
    }

    @Test
    void shouldAuthenticateSuccessfully() {
        RegisterRequestDTO registerDTO = UserTestFactory.buildRegisterRequest();
        User user = userService.registerUser(registerDTO, UserRole.USER);

        AuthenticationRequestDTO loginDTO = new AuthenticationRequestDTO(registerDTO.email(), registerDTO.password());

        Authentication authenticationMock = new UsernamePasswordAuthenticationToken(user, null);
        when(authenticationManager.authenticate(any())).thenReturn(authenticationMock);

        var response = userService.authenticate(loginDTO, authenticationManager);

        assertNotNull(response.token());
        assertFalse(response.token().isBlank());
    }

    @Test
    void shouldFailAuthenticationWithWrongPassword() {
        RegisterRequestDTO registerDTO = UserTestFactory.buildRegisterRequest();
        userService.registerUser(registerDTO, UserRole.USER);

        AuthenticationRequestDTO loginDTO = new AuthenticationRequestDTO(registerDTO.email(), "wrongpassword");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Credenciais inválidas"));

        Exception exception = assertThrows(BadCredentialsException.class, () -> {
            userService.authenticate(loginDTO, authenticationManager);
        });

        assertEquals("Credenciais inválidas", exception.getMessage());
    }
}
