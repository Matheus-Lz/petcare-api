package com.petcare.petcare_api.coredomain.service;

import com.petcare.petcare_api.application.dto.workingPeriod.WorkingPeriodRequestDTO;
import com.petcare.petcare_api.coredomain.model.WorkingPeriod;
import com.petcare.petcare_api.infrastructure.repository.WorkingPeriodRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class WorkingPeriodServiceTest {

    @Autowired
    private WorkingPeriodService service;

    @Autowired
    private WorkingPeriodRepository repository;

    @Test
    void shouldCreateWorkingPeriodSuccessfully() {
        WorkingPeriodRequestDTO dto = new WorkingPeriodRequestDTO(DayOfWeek.MONDAY,
                LocalTime.of(9, 0), LocalTime.of(12, 0));

        WorkingPeriod saved = service.create(dto);

        assertNotNull(saved.getId());
        assertEquals(DayOfWeek.MONDAY, saved.getDayOfWeek());
        assertEquals(LocalTime.of(9, 0), saved.getStartTime());
        assertEquals(LocalTime.of(12, 0), saved.getEndTime());
    }

    @Test
    void shouldThrowExceptionWhenTimeOverlaps() {
        service.create(new WorkingPeriodRequestDTO(DayOfWeek.MONDAY,
                LocalTime.of(9, 0), LocalTime.of(12, 0)));

        WorkingPeriodRequestDTO overlappingDto = new WorkingPeriodRequestDTO(DayOfWeek.MONDAY,
                LocalTime.of(11, 0), LocalTime.of(13, 0));

        assertThrows(IllegalArgumentException.class, () -> service.create(overlappingDto));
    }

    @Test
    void shouldUpdateWorkingPeriod() {
        WorkingPeriod period = service.create(new WorkingPeriodRequestDTO(DayOfWeek.MONDAY,
                LocalTime.of(9, 0), LocalTime.of(12, 0)));

        WorkingPeriodRequestDTO updateDto = new WorkingPeriodRequestDTO(DayOfWeek.MONDAY,
                LocalTime.of(13, 0), LocalTime.of(15, 0));

        WorkingPeriod updated = service.update(period.getId(), updateDto);

        assertEquals(LocalTime.of(13, 0), updated.getStartTime());
        assertEquals(LocalTime.of(15, 0), updated.getEndTime());
    }

    @Test
    void shouldDeleteWorkingPeriod() {
        WorkingPeriod saved = service.create(new WorkingPeriodRequestDTO(DayOfWeek.TUESDAY,
                LocalTime.of(10, 0), LocalTime.of(11, 0)));

        service.delete(saved.getId());

        assertFalse(repository.existsById(saved.getId()));
    }
}

