package com.petcare.petcare_api.coredomain.service;

import com.petcare.petcare_api.application.dto.user.AuthenticationRequestDTO;
import com.petcare.petcare_api.application.dto.user.RegisterRequestDTO;
import com.petcare.petcare_api.application.dto.user.UpdateUserRequestDTO;
import com.petcare.petcare_api.coredomain.model.user.User;
import com.petcare.petcare_api.coredomain.model.user.enums.UserRole;
import com.petcare.petcare_api.infrastructure.exception.UserExceptions;
import com.petcare.petcare_api.infrastructure.repository.UserRepository;
import com.petcare.petcare_api.utils.UserTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private TokenService tokenService;
    @Mock
    private EmailService emailService;
    @Mock
    private PasswordEncoder passwordEncoder;
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
        when(passwordEncoder.encode(registerDTO.password())).thenReturn("hashedPwd");
        when(userRepository.save(any())).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId("123");
            return u;
        });

        User user = userService.registerUser(registerDTO, UserRole.USER);

        assertNotNull(user.getId());
        assertEquals(registerDTO.email(), user.getEmail());
        assertEquals("hashedPwd", user.getPassword());
    }

    @Test
    void shouldNotAllowDuplicateEmail() {
        when(userRepository.findByEmail(registerDTO.email())).thenReturn(new User());
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(registerDTO, UserRole.USER));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        String fakeId = "00000000-0000-0000-0000-000000000000";
        when(userRepository.findById(fakeId)).thenReturn(Optional.empty());
        Exception ex = assertThrows(IllegalArgumentException.class, () -> userService.getById(fakeId));
        assertEquals("Usuário não encontrado", ex.getMessage());
    }

    @Test
    void shouldAuthenticateSuccessfully() {
        AuthenticationRequestDTO loginDTO = new AuthenticationRequestDTO(registerDTO.email(), registerDTO.password());
        User principal = new User();
        principal.setEmail(registerDTO.email());
        principal.setRole(UserRole.USER);
        principal.setName("John");
        principal.setId("id-1");

        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(tokenService.generateToken(principal.getEmail())).thenReturn("fake-token");

        var response = userService.authenticate(loginDTO, authenticationManager);

        assertNotNull(response);
        assertEquals("fake-token", response.token());
        assertEquals("USER", response.role());
        assertEquals("John", response.name());
        assertEquals("id-1", response.userId());
    }

    @Test
    void shouldFailAuthenticationWithWrongPassword() {
        AuthenticationRequestDTO loginDTO = new AuthenticationRequestDTO(registerDTO.email(), "wrong");
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Credenciais inválidas"));
        assertThrows(BadCredentialsException.class, () -> userService.authenticate(loginDTO, authenticationManager));
    }

    @Test
    void shouldUpdateUserBasicFields() {
        User user = User.builder().email("a@a.com").name("A").cpfCnpj("26001636036").password("p12345").role(UserRole.USER).build();
        user.setId("u1");
        mockAuthenticated(user);
        when(userRepository.findById("u1")).thenReturn(Optional.of(user));

        UpdateUserRequestDTO dto = new UpdateUserRequestDTO(null, null, "12224730039", "B");
        userService.updateUser("u1", dto);

        assertEquals("B", user.getName());
        assertEquals("12224730039", user.getCpfCnpj());
        verify(userRepository).save(user);
    }

    @Test
    void shouldNotAllowUpdateAnotherUser() {
        User current = User.builder().password("p12345").role(UserRole.USER).build();
        current.setId("u1");
        mockAuthenticated(current);

        User target = User.builder().password("p12345").role(UserRole.USER).build();
        target.setId("u2");
        when(userRepository.findById("u2")).thenReturn(Optional.of(target));

        UpdateUserRequestDTO dto = new UpdateUserRequestDTO(null, null, null, null);
        assertThrows(IllegalArgumentException.class, () -> userService.updateUser("u2", dto));
    }

    @Test
    void shouldRequireCurrentPasswordOnChange() {
        User user = User.builder().password("hash123").role(UserRole.USER).build();
        user.setId("u1");
        mockAuthenticated(user);
        when(userRepository.findById("u1")).thenReturn(Optional.of(user));

        UpdateUserRequestDTO dto = new UpdateUserRequestDTO("1", null, "1", "A");
        assertThrows(UserExceptions.CurrentPasswordRequiredException.class, () -> userService.updateUser("u1", dto));
    }

    @Test
    void shouldRejectInvalidCurrentPassword() {
        User user = User.builder().password("hash123").role(UserRole.USER).build();
        user.setId("u1");
        mockAuthenticated(user);
        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpass", "hash123")).thenReturn(false);

        UpdateUserRequestDTO dto = new UpdateUserRequestDTO(
                "newPassword",
                "wrongpass",
                "12345678901",
                "A"
        );

        assertThrows(UserExceptions.InvalidCurrentPasswordException.class,
                () -> userService.updateUser("u1", dto));
    }

    @Test
    void shouldChangePasswordSuccessfully() {
        User user = User.builder().email("xyz@gmail.com").password("hash123456").cpfCnpj("86939014004").role(UserRole.USER).build();
        user.setId("u1");
        mockAuthenticated(user);
        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old123456", "hash123456")).thenReturn(true);
        when(passwordEncoder.encode("new123456")).thenReturn("hash2123456");

        UpdateUserRequestDTO dto = new UpdateUserRequestDTO("new123456", "old123456", null, null);
        userService.updateUser("u1", dto);

        assertEquals("hash2123456", user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void shouldSendResetToken() {
        User user = new User();
        user.setEmail("x@y.com");
        when(userRepository.findUserByEmail("x@y.com")).thenReturn(user);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(emailService.resetToken(anyString())).thenAnswer(i -> "body " + i.getArgument(0));

        userService.sendResetToken("x@y.com");

        assertNotNull(user.getResetToken());
        assertNotNull(user.getResetTokenExpiration());
        verify(emailService).sendHtml(eq("x@y.com"), anyString(), contains(user.getResetToken()));
    }

    @Test
    void shouldResetPassword() {
        User user = new User();
        user.setEmail("1@gmail.com");
        user.setCpfCnpj("23602088000117");
        user.setResetToken("t");
        user.setResetToken("t");
        user.setResetTokenExpiration(LocalDateTime.now().plusMinutes(5));
        when(userRepository.findByResetToken("t")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("p2")).thenReturn("h2hash");

        userService.resetPassword("t", "p2");

        assertNull(user.getResetToken());
        assertNull(user.getResetTokenExpiration());
        assertEquals("h2hash", user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void shouldThrowOnExpiredToken() {
        User user = new User();
        user.setResetToken("t");
        user.setResetTokenExpiration(LocalDateTime.now().minusMinutes(1));
        when(userRepository.findByResetToken("t")).thenReturn(Optional.of(user));
        assertThrows(IllegalArgumentException.class, () -> userService.resetPassword("t", "p2"));
    }

    @Test
    void shouldLoadUserByUsername() {
        User u = new User();
        when(userRepository.findByEmail("a@a.com")).thenReturn(u);
        assertSame(u, userService.loadUserByUsername("a@a.com"));
    }

    @Test
    void shouldFailLoadUserByUsername() {
        when(userRepository.findByEmail("a@a.com")).thenReturn(null);
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("a@a.com"));
    }

    private void mockAuthenticated(User principal) {
        Authentication auth =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContext ctx = mock(SecurityContext.class);
        when(ctx.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(ctx);
    }

    @Test
    void shouldReturnNullWhenNoAuthentication() {
        SecurityContext ctx = mock(SecurityContext.class);
        when(ctx.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(ctx);

        assertNull(userService.getCurrentUser());
    }

    @Test
    void shouldReturnNullWhenAnonymousUser() {
        Authentication auth =
                new UsernamePasswordAuthenticationToken("anonymousUser", null, java.util.Collections.emptyList());
        SecurityContext ctx = mock(SecurityContext.class);
        when(ctx.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(ctx);

        assertNull(userService.getCurrentUser());
    }

    @Test
    void shouldThrowWhenEmailNotFoundOnSendResetToken() {
        when(userRepository.findUserByEmail("naoexiste@x.com")).thenReturn(null);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.sendResetToken("naoexiste@x.com"));
        assertEquals("Email não encontrado", ex.getMessage());
    }

    @Test
    void shouldIgnoreBlankFieldsOnUpdate() {
        User user = User.builder()
                .email("old@mail.com").name("Old").cpfCnpj("12224730039")
                .password("123456").role(UserRole.USER).build();
        user.setId("u1");
        mockAuthenticated(user);
        when(userRepository.findById("u1")).thenReturn(Optional.of(user));

        UpdateUserRequestDTO dto = new UpdateUserRequestDTO("  ", null, " ", "  ");
        userService.updateUser("u1", dto);

        assertEquals("old@mail.com", user.getEmail());
        assertEquals("Old", user.getName());
        assertEquals("12224730039", user.getCpfCnpj());
        assertEquals("123456", user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void shouldRegisterUserWithDefaultRoleUSER() {
        when(userRepository.findByEmail(registerDTO.email())).thenReturn(null);
        when(passwordEncoder.encode(registerDTO.password())).thenReturn("hashedPwd");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        userService.registerUser(registerDTO);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals(UserRole.USER, captor.getValue().getRole());
        assertEquals("hashedPwd", captor.getValue().getPassword());
    }

}
