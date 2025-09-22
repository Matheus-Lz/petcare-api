package com.petcare.petcare_api.coredomain.model;

import com.petcare.petcare_api.coredomain.model.user.User;
import com.petcare.petcare_api.coredomain.model.user.enums.UserRole;
import com.petcare.petcare_api.infrastructure.exception.UserExceptions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserModelTest {

    private User.UserBuilder createValidUserBuilder() {
        return User.builder()
                .email("test@example.com")
                .password("123456")
                .cpfCnpj("23602088000117")
                .name("Test User");
    }

    @Test
    void shouldThrowWhenEmailIsEmpty() {
        User userWithEmptyEmail = createValidUserBuilder().email("").build();
        User userWithNullEmail = createValidUserBuilder().email(null).build();
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class, userWithEmptyEmail::validate);
        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, userWithNullEmail::validate);
        assertEquals("O email não pode ser vazio", ex1.getMessage());
        assertEquals("O email não pode ser vazio", ex2.getMessage());
    }

    @Test
    void shouldThrowWhenPasswordIsTooShort() {
        User user = createValidUserBuilder().password("12345").build();
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, user::validate);
        assertEquals("A senha deve ter pelo menos 6 caracteres", ex.getMessage());
    }

    @Test
    void shouldThrowWhenCpfCnpjIsInvalid() {
        User user = createValidUserBuilder().cpfCnpj("12345678910").build();
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, user::validate);
        assertEquals("O Cpf/Cnpj informado é inválido", ex.getMessage());
    }

    @Test
    void shouldValidateSuccessfully() {
        User validUser = createValidUserBuilder().build();
        assertDoesNotThrow(validUser::validate);
    }

    @Test
    void shouldExposeAuthoritiesForEachRole() {
        User u1 = createValidUserBuilder().role(UserRole.USER).build();
        User u2 = createValidUserBuilder().role(UserRole.EMPLOYEE).build();
        User u3 = createValidUserBuilder().role(UserRole.SUPER_ADMIN).build();
        assertTrue(u1.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
        assertTrue(u2.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_EMPLOYEE")));
        assertTrue(u3.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN")));
    }

    @Test
    void shouldThrowWhenRoleIsNullInAuthorities() {
        User u = createValidUserBuilder().role(null).build();
        assertThrows(UserExceptions.UserAuthoritiesFetchException.class, u::getAuthorities);
    }

    @Test
    void shouldExposeUserDetailsFields() {
        User u = createValidUserBuilder().role(UserRole.USER).build();
        assertEquals("test@example.com", u.getUsername());
        assertEquals("123456", u.getPassword());
        assertTrue(u.isAccountNonExpired());
        assertTrue(u.isAccountNonLocked());
        assertTrue(u.isCredentialsNonExpired());
        assertTrue(u.isEnabled());
    }
}
