package com.petcare.petcare_api.utils;

import com.petcare.petcare_api.coredomain.service.EmailService;
import com.petcare.petcare_api.coredomain.service.UserService;
import com.petcare.petcare_api.coredomain.service.TokenService;
import com.petcare.petcare_api.infrastructure.repository.UserRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@TestConfiguration
public class TestServiceConfig {

    @Bean
    public UserService userService(UserRepository userRepository, EmailService emailService, TokenService tokenService, PasswordEncoder passwordEncoder) {
        return new UserService(tokenService, emailService, userRepository, passwordEncoder);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

