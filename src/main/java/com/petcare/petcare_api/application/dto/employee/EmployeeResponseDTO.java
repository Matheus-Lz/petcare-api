package com.petcare.petcare_api.application.dto.employee;

import com.petcare.petcare_api.application.dto.user.UserResponse;
import com.petcare.petcare_api.coredomain.model.Employee;
import lombok.Data;

import java.util.List;

@Data
public class EmployeeResponseDTO {
    private String id;
    private UserResponse user;
    private List<PetServiceEmployeeResponse> petServiceList;

    public EmployeeResponseDTO(Employee employee) {
        this.id = employee.getId();
        this.user = new UserResponse(employee.getUser());
        this.petServiceList = employee.getPetServiceList().stream()
                .map(PetServiceEmployeeResponse::new)
                .toList();
    }
}
