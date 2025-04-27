package com.petcare.petcare_api.application.dto.employee;

import com.petcare.petcare_api.coredomain.model.Employee;
import com.petcare.petcare_api.infrastructure.baseEntities.BaseModel;
import lombok.Data;

import java.util.List;

@Data
public class EmployeeResponseDTO {
    private String id;
    private String userId;
    private List<String> serviceIds;

    public EmployeeResponseDTO(Employee employee) {
        this.id = employee.getId();
        this.userId = employee.getUser().getId();
        this.serviceIds = employee.getPetServiceList().stream()
                .map(BaseModel::getId)
                .toList();
    }
}
