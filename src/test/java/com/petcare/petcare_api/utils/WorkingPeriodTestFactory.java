package com.petcare.petcare_api.utils;

import com.petcare.petcare_api.application.dto.workingperiod.WorkingPeriodRequestDTO;
import com.petcare.petcare_api.coredomain.model.WorkingPeriod;
import com.petcare.petcare_api.infrastructure.repository.WorkingPeriodRepository;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Component
public class WorkingPeriodTestFactory {

    private final WorkingPeriodRepository workingPeriodRepository;

    public WorkingPeriodTestFactory(WorkingPeriodRepository workingPeriodRepository) {
        this.workingPeriodRepository = workingPeriodRepository;
    }

    public static WorkingPeriodRequestDTO buildRequest() {
        return new WorkingPeriodRequestDTO(
                DayOfWeek.MONDAY,
                LocalTime.of(9, 0),
                LocalTime.of(17, 0)
        );
    }

    public static WorkingPeriod buildEntity() {
        return buildEntityWithDay(DayOfWeek.MONDAY);
    }

    public static WorkingPeriod buildEntityWithDay(DayOfWeek dayOfWeek) {
        return WorkingPeriod.builder()
                .dayOfWeek(dayOfWeek)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .build();
    }

    public void persistWorkingPeriod(DayOfWeek dayOfWeek) {
        WorkingPeriod entity = WorkingPeriod.builder()
                .dayOfWeek(dayOfWeek)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .build();
        workingPeriodRepository.save(entity);
    }
}