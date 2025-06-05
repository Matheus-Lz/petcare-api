package com.petcare.petcare_api.application.controller;

import com.petcare.petcare_api.application.dto.user.*;
import com.petcare.petcare_api.coredomain.model.User;
import com.petcare.petcare_api.coredomain.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO requestDTO) {
        service.registerUser(requestDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody UpdateUserRequestDTO updateDTO) {
        service.updateUser(id, updateDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<Page<User>> listUsers(@RequestParam(defaultValue = "0") Integer page,
                                                @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(service.listUsers(page, size));
    }
}
