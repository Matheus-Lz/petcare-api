package com.petcare.petcare_api.application.dto.scheduling;

import com.petcare.petcare_api.coredomain.model.scheduling.Scheduling;
import com.petcare.petcare_api.coredomain.model.scheduling.enums.SchedulingStatus;

import java.time.LocalDateTime;

public record SchedulingResponseDTO(
        String id,
        String userId,
        String employeeId,
        String petServiceId,
        LocalDateTime schedulingHour,
        SchedulingStatus status
) {
    public SchedulingResponseDTO(Scheduling scheduling) {
        this(scheduling.getId(),
                scheduling.getUser().getId(),
                scheduling.getEmployee() != null ? scheduling.getEmployee().getId() : null,
                scheduling.getPetService().getId(),
                scheduling.getSchedulingHour(),
                scheduling.getStatus());
    }
}
