package com.petcare.petcare_api.application.dto.employee;

import com.petcare.petcare_api.application.dto.user.UserResponse;
import com.petcare.petcare_api.coredomain.model.Employee;

import java.util.List;

public record EmployeeResponseDTO(
        String id,
        UserResponse user,
        List<PetServiceEmployeeResponse> petServiceList) {
    public EmployeeResponseDTO(Employee employee) {
        this(employee.getId(),
                new UserResponse(employee.getUser()),
                employee.getPetServiceList().stream()
                        .map(PetServiceEmployeeResponse::new)
                        .toList());
    }
}
