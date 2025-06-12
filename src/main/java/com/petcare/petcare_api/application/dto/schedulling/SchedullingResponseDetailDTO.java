package com.petcare.petcare_api.application.dto.schedulling;

import com.petcare.petcare_api.application.dto.employee.EmployeeResponseDTO;
import com.petcare.petcare_api.application.dto.petServices.PetServiceResponseDTO;
import com.petcare.petcare_api.coredomain.model.schedulling.Schedulling;
import com.petcare.petcare_api.coredomain.model.schedulling.enums.SchedullingStatus;

import java.time.LocalDateTime;

public record SchedullingResponseDetailDTO(
        String id,
        EmployeeResponseDTO employee,
        PetServiceResponseDTO petService,
        LocalDateTime schedullingHour,
        SchedullingStatus status
) {
    public SchedullingResponseDetailDTO(Schedulling schedulling) {
        this(schedulling.getId(),
                schedulling.getEmployee() != null ? new EmployeeResponseDTO(schedulling.getEmployee()) : null,
                new PetServiceResponseDTO(schedulling.getPetService()),
                schedulling.getSchedullingHour(),
                schedulling.getStatus());
    }
}
