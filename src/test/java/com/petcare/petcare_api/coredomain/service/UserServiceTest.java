package com.petcare.petcare_api.coredomain.service;

import com.petcare.petcare_api.application.dto.user.RegisterRequestDTO;
import com.petcare.petcare_api.application.dto.user.UpdateUserRequestDTO;
import com.petcare.petcare_api.infrastructure.enums.user.UserRole;
import com.petcare.petcare_api.infrastructure.repository.UserRepository;
import com.petcare.petcare_api.utils.UserTestFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({UserService.class, TokenService.class})
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

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
    void shouldUpdateUserSuccessfully() {
        var request = UserTestFactory.buildRegisterRequest();
        var user = userService.registerUser(request, UserRole.USER);

        UpdateUserRequestDTO updateDTO = UserTestFactory.buildUpdateRequest();

        userService.updateUser(user.getId(), updateDTO);

        var updatedUser = userService.getById(user.getId());
        assertEquals(updateDTO.email(), updatedUser.getEmail());
        assertEquals(updateDTO.name(), updatedUser.getName());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        String fakeId = "00000000-0000-0000-0000-000000000000";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.getById(fakeId));

        assertEquals("Usuário não encontrado", exception.getMessage());
    }
}
