package com.petcare.petcare_api.application.dto.employee;

import com.petcare.petcare_api.application.dto.user.RegisterRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateEmployeeRequestDTO(

        @NotNull(message = "Os dados de usuário são obrigatórios")
        @Valid
        RegisterRequestDTO user,

        @NotNull(message = "A lista de serviços não pode ser nula")
        List<String> serviceIds

) {}
