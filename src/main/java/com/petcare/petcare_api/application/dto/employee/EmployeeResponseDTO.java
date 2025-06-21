package com.petcare.petcare_api.application.dto.employee;

import com.petcare.petcare_api.application.dto.user.UserResponseDTO;
import com.petcare.petcare_api.coredomain.model.Employee;

import java.util.List;

public record EmployeeResponseDTO(
        String id,
        UserResponseDTO user,
        List<PetServiceEmployeeResponse> petServiceList) {
    public EmployeeResponseDTO(Employee employee) {
        this(employee.getId(),
                new UserResponseDTO(employee.getUser()),
                employee.getPetServiceList().stream()
                        .map(PetServiceEmployeeResponse::new)
                        .toList());
    }
}
