package com.petcare.petcare_api.application.dto.scheduling;

import com.petcare.petcare_api.application.dto.employee.EmployeeResponseDTO;
import com.petcare.petcare_api.application.dto.petservices.PetServiceResponseDTO;
import com.petcare.petcare_api.coredomain.model.scheduling.Scheduling;
import com.petcare.petcare_api.coredomain.model.scheduling.enums.SchedulingStatus;

import java.time.LocalDateTime;

public record SchedulingResponseDetailDTO(
        String id,
        EmployeeResponseDTO employee,
        PetServiceResponseDTO petService,
        LocalDateTime schedulingHour,
        SchedulingStatus status
) {
    public SchedulingResponseDetailDTO(Scheduling scheduling) {
        this(scheduling.getId(),
                scheduling.getEmployee() != null ? new EmployeeResponseDTO(scheduling.getEmployee()) : null,
                new PetServiceResponseDTO(scheduling.getPetService()),
                scheduling.getSchedulingHour(),
                scheduling.getStatus());
    }
}
