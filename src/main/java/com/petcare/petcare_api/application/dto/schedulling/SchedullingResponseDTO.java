package com.petcare.petcare_api.application.dto.schedulling;

import java.time.LocalDateTime;

public record SchedullingResponseDTO(
        String id,
        String userId,
        String employeeId,
        String petServiceId,
        LocalDateTime schedullingHour
) {}
