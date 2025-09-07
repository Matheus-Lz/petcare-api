package com.petcare.petcare_api.coredomain.service;

import com.petcare.petcare_api.application.dto.user.AuthenticationRequestDTO;
import com.petcare.petcare_api.application.dto.user.RegisterRequestDTO;
import com.petcare.petcare_api.coredomain.model.user.User;
import com.petcare.petcare_api.coredomain.model.user.enums.UserRole;
import com.petcare.petcare_api.infrastructure.repository.UserRepository;
import com.petcare.petcare_api.utils.UserTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    @Mock()
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    private RegisterRequestDTO registerDTO;

    @BeforeEach
    void setUp() {
        registerDTO = UserTestFactory.buildRegisterRequest();
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        when(userRepository.findByEmail(registerDTO.email())).thenReturn(null);
        when(userRepository.save(any())).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId("123");
            return user;
        });

        User user = userService.registerUser(registerDTO, UserRole.USER);

        assertNotNull(user.getId());
        assertEquals(registerDTO.email(), user.getEmail());
        assertEquals(registerDTO.name(), user.getName());
    }

    @Test
    void shouldNotAllowDuplicateEmail() {
        when(userRepository.findByEmail(registerDTO.email()))
                .thenReturn(new User());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.registerUser(registerDTO, UserRole.USER));

        assertEquals("Email já em uso", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        String fakeId = "00000000-0000-0000-0000-000000000000";

        when(userRepository.findById(fakeId)).thenReturn(java.util.Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.getById(fakeId));

        assertEquals("Usuário não encontrado", exception.getMessage());
    }

    @Test
    void shouldAuthenticateSuccessfully() {
        AuthenticationRequestDTO loginDTO = new AuthenticationRequestDTO(registerDTO.email(), registerDTO.password());
        User fakeUser = new User();
        fakeUser.setEmail(registerDTO.email());
        fakeUser.setRole(UserRole.USER);

        Authentication authenticationMock = new UsernamePasswordAuthenticationToken(fakeUser, null);
        when(authenticationManager.authenticate(any())).thenReturn(authenticationMock);
        when(tokenService.generateToken(fakeUser.getEmail())).thenReturn("fake-token");

        var response = userService.authenticate(loginDTO, authenticationManager);

        assertNotNull(response);
        assertEquals("fake-token", response.token());
    }

    @Test
    void shouldFailAuthenticationWithWrongPassword() {
        AuthenticationRequestDTO loginDTO = new AuthenticationRequestDTO(registerDTO.email(), "wrongpassword");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Credenciais inválidas"));

        Exception exception = assertThrows(BadCredentialsException.class, () ->
                userService.authenticate(loginDTO, authenticationManager));

        assertEquals("Credenciais inválidas", exception.getMessage());
    }
}
