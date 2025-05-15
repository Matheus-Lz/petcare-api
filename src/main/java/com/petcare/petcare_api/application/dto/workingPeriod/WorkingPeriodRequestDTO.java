package com.petcare.petcare_api.application.dto.workingPeriod;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record WorkingPeriodRequestDTO(
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        LocalTime endTime
) {}
