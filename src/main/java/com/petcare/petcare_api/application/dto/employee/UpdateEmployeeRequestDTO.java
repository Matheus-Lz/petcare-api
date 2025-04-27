package com.petcare.petcare_api.application.dto.employee;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UpdateEmployeeRequestDTO(

        @NotNull(message = "A lista de serviços não pode ser nula")
        List<String> serviceIds

) {}
