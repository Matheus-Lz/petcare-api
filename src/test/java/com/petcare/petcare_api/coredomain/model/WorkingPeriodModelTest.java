package com.petcare.petcare_api.coredomain.model;

import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class WorkingPeriodModelTest {

    @Test
    void shouldThrowWhenStartTimeAfterEndTime() {
        WorkingPeriod invalid = new WorkingPeriod(DayOfWeek.THURSDAY,
                LocalTime.of(15, 0),
                LocalTime.of(14, 0));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, invalid::validateInternalState);
        assertTrue(ex.getMessage().contains("deve ser anterior"));
    }

    @Test
    void shouldValidateSuccessfully() {
        WorkingPeriod valid = new WorkingPeriod(DayOfWeek.FRIDAY,
                LocalTime.of(9, 0),
                LocalTime.of(12, 0));

        assertDoesNotThrow(valid::validateInternalState);
    }
}

