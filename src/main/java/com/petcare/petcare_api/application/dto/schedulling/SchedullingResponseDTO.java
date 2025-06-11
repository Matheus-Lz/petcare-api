package com.petcare.petcare_api.application.dto.schedulling;

import com.petcare.petcare_api.coredomain.model.schedulling.enums.SchedullingStatus;

import java.time.LocalDateTime;

public record SchedullingResponseDTO(
        String id,
        String userId,
        String employeeId,
        String petServiceId,
        LocalDateTime schedullingHour,
        SchedullingStatus status
) {}
