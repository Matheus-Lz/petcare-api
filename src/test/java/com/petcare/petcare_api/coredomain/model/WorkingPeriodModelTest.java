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

    @Test
    void shouldThrowWhenDayIsNull() {
        WorkingPeriod wp = new WorkingPeriod(null, LocalTime.of(9, 0), LocalTime.of(10, 0));
        assertThrows(IllegalArgumentException.class, wp::validateInternalState);
    }

    @Test
    void shouldThrowWhenTimesAreNull() {
        WorkingPeriod wp1 = new WorkingPeriod(DayOfWeek.MONDAY, null, LocalTime.of(10, 0));
        WorkingPeriod wp2 = new WorkingPeriod(DayOfWeek.MONDAY, LocalTime.of(9, 0), null);
        assertThrows(IllegalArgumentException.class, wp1::validateInternalState);
        assertThrows(IllegalArgumentException.class, wp2::validateInternalState);
    }

    @Test
    void shouldThrowWhenTimesAreEqual() {
        WorkingPeriod wp = new WorkingPeriod(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(9, 0));
        assertThrows(IllegalArgumentException.class, wp::validateInternalState);
    }
}
