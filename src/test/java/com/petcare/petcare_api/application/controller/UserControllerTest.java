package com.petcare.petcare_api.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petcare.petcare_api.application.dto.user.*;
import com.petcare.petcare_api.coredomain.model.user.User;
import com.petcare.petcare_api.coredomain.service.UserService;
import com.petcare.petcare_api.utils.UserTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserController userController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void shouldRegisterUser() throws Exception {
        RegisterRequestDTO request = UserTestFactory.buildRegisterRequest();

        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRejectRegisterWithInvalidPayload() throws Exception {
        RegisterRequestDTO invalid = new RegisterRequestDTO("invalid", "123", "", "");

        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdateUser() throws Exception {
        UpdateUserRequestDTO updateRequest = UserTestFactory.buildUpdateRequest();

        mockMvc.perform(put("/user/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldLoginSuccessfully() throws Exception {
        AuthenticationRequestDTO authRequest = UserTestFactory.buildAuthRequest();
        AuthenticationResponseDTO authResponse = new AuthenticationResponseDTO("mocked-token", "role", "name", "userId");

        when(userService.authenticate(authRequest, authenticationManager)).thenReturn(authResponse);

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked-token"))
                .andExpect(jsonPath("$.role").value("role"))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.userId").value("userId"));
    }

    @Test
    void shouldRejectLoginWithInvalidPayload() throws Exception {
        AuthenticationRequestDTO invalid = new AuthenticationRequestDTO("not-an-email", null);

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnUserById() throws Exception {
        User user = UserTestFactory.buildUser();
        UserResponseDTO userResponse = new UserResponseDTO(user);

        when(userService.getCurrentUser()).thenReturn(user);

        mockMvc.perform(get("/user/current-user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(userResponse.getEmail()))
                .andExpect(jsonPath("$.cpfCnpj").value(userResponse.getCpfCnpj()))
                .andExpect(jsonPath("$.name").value(userResponse.getName()));
    }

    @Test
    void shouldSendForgotPasswordEmail() throws Exception {
        ForgotPasswordRequestDTO dto = new ForgotPasswordRequestDTO("test@example.com");

        mockMvc.perform(post("/user/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldResetPassword() throws Exception {
        ResetPasswordDTO dto = new ResetPasswordDTO("reset-token-123", "newSecurePassword");

        mockMvc.perform(post("/user/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }
}
