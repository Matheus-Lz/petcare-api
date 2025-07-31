package com.petcare.petcare_api.utils;

import com.petcare.petcare_api.application.dto.workingPeriod.WorkingPeriodRequestDTO;
import com.petcare.petcare_api.coredomain.model.WorkingPeriod;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class WorkingPeriodTestFactory {

    public static WorkingPeriodRequestDTO buildRequest() {
        return new WorkingPeriodRequestDTO(
                DayOfWeek.MONDAY,
                LocalTime.of(9, 0),
                LocalTime.of(17, 0)
        );
    }

    public static WorkingPeriod buildEntity() {
        WorkingPeriod entity = new WorkingPeriod();
        entity.setId("1");
        entity.setDayOfWeek(DayOfWeek.MONDAY);
        entity.setStartTime(LocalTime.of(9, 0));
        entity.setEndTime(LocalTime.of(17, 0));
        return entity;
    }
}
