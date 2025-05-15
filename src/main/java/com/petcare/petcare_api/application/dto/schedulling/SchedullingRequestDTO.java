package com.petcare.petcare_api.application.dto.schedulling;

import java.time.LocalDateTime;

public record SchedullingRequestDTO(
        String userId,
        String petServiceId,
        LocalDateTime schedullingHour
) {}
