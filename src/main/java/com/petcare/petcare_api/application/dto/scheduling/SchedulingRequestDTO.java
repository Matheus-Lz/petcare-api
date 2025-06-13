package com.petcare.petcare_api.application.dto.scheduling;

import java.time.LocalDateTime;

public record SchedulingRequestDTO(
        String petServiceId,
        LocalDateTime schedulingHour
) {}
