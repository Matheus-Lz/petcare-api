package com.petcare.petcare_api.application.dto.workingPeriod;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record WorkingPeriodResponseDTO(
        String id,
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        LocalTime endTime
) {}
