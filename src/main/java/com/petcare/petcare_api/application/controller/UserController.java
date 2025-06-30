package com.petcare.petcare_api.application.controller;

import com.petcare.petcare_api.application.dto.user.*;
import com.petcare.petcare_api.coredomain.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService service;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserController(UserService service, AuthenticationManager authenticationManager) {
        this.service = service;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDTO> login(@Valid @RequestBody AuthenticationRequestDTO requestDTO) {
        AuthenticationResponseDTO user = service.authenticate(requestDTO, authenticationManager);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequestDTO requestDTO) {
        service.registerUser(requestDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById() {
        return ResponseEntity.ok(new UserResponseDTO(service.getCurrentUser()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable String id, @RequestBody UpdateUserRequestDTO updateDTO) {
        service.updateUser(id, updateDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestBody ForgotPasswordRequestDTO dto) {
        service.sendResetToken(dto.email());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordDTO dto) {
        service.resetPassword(dto.token(), dto.newPassword());
        return ResponseEntity.ok().build();
    }
}
