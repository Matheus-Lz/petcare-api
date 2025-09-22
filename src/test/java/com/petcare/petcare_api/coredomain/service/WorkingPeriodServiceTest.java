package com.petcare.petcare_api.coredomain.service;

import com.petcare.petcare_api.application.dto.workingperiod.WorkingPeriodRequestDTO;
import com.petcare.petcare_api.coredomain.model.WorkingPeriod;
import com.petcare.petcare_api.infrastructure.repository.WorkingPeriodRepository;
import com.petcare.petcare_api.utils.WorkingPeriodTestFactory;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class WorkingPeriodServiceTest {

    @Autowired
    private WorkingPeriodService service;

    @Autowired
    private WorkingPeriodRepository repository;

    @Autowired
    private WorkingPeriodTestFactory workingPeriodTestFactory;

    @Test
    void shouldCreateWorkingPeriodSuccessfully() {
        WorkingPeriodRequestDTO dto = new WorkingPeriodRequestDTO(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(12, 0));
        WorkingPeriod saved = service.create(dto);
        assertNotNull(saved.getId());
        assertEquals(DayOfWeek.MONDAY, saved.getDayOfWeek());
        assertEquals(LocalTime.of(9, 0), saved.getStartTime());
        assertEquals(LocalTime.of(12, 0), saved.getEndTime());
    }

    @Test
    void shouldThrowExceptionWhenTimeOverlaps() {
        workingPeriodTestFactory.persistWorkingPeriod(DayOfWeek.MONDAY);
        WorkingPeriodRequestDTO overlappingDto = new WorkingPeriodRequestDTO(DayOfWeek.MONDAY, LocalTime.of(11, 0), LocalTime.of(13, 0));
        assertThrows(IllegalArgumentException.class, () -> service.create(overlappingDto));
    }

    @Test
    void shouldAllowTouchingIntervals() {
        service.create(new WorkingPeriodRequestDTO(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(12, 0)));
        WorkingPeriodRequestDTO dto = new WorkingPeriodRequestDTO(DayOfWeek.MONDAY, LocalTime.of(12, 0), LocalTime.of(13, 0));
        WorkingPeriod saved = service.create(dto);
        assertEquals(LocalTime.of(12, 0), saved.getStartTime());
    }

    @Test
    void shouldUpdateWorkingPeriod() {
        WorkingPeriod period = service.create(new WorkingPeriodRequestDTO(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(12, 0)));
        WorkingPeriodRequestDTO updateDto = new WorkingPeriodRequestDTO(DayOfWeek.MONDAY, LocalTime.of(13, 0), LocalTime.of(15, 0));
        WorkingPeriod updated = service.update(period.getId(), updateDto);
        assertEquals(LocalTime.of(13, 0), updated.getStartTime());
        assertEquals(LocalTime.of(15, 0), updated.getEndTime());
    }

    @Test
    void shouldThrowWhenUpdatingNotFound() {
        WorkingPeriodRequestDTO updateDto = new WorkingPeriodRequestDTO(DayOfWeek.TUESDAY, LocalTime.of(8, 0), LocalTime.of(9, 0));
        assertThrows(EntityNotFoundException.class, () -> service.update("nao-existe", updateDto));
    }

    @Test
    void shouldFindAllSortedByStartTime() {
        service.create(new WorkingPeriodRequestDTO(DayOfWeek.WEDNESDAY, LocalTime.of(13, 0), LocalTime.of(14, 0)));
        service.create(new WorkingPeriodRequestDTO(DayOfWeek.WEDNESDAY, LocalTime.of(9, 0), LocalTime.of(10, 0)));
        List<WorkingPeriod> all = service.findAll();
        assertTrue(all.size() >= 2);
        for (int i = 1; i < all.size(); i++) {
            assertFalse(all.get(i).getStartTime().isBefore(all.get(i - 1).getStartTime()));
        }
    }

    @Test
    void shouldFindById() {
        WorkingPeriod saved = service.create(new WorkingPeriodRequestDTO(DayOfWeek.THURSDAY, LocalTime.of(10, 0), LocalTime.of(12, 0)));
        WorkingPeriod found = service.findById(saved.getId());
        assertEquals(saved.getId(), found.getId());
    }

    @Test
    void shouldThrowWhenFindByIdNotFound() {
        assertThrows(EntityNotFoundException.class, () -> service.findById("x"));
    }

    @Test
    void shouldDeleteWorkingPeriod() {
        WorkingPeriod saved = service.create(new WorkingPeriodRequestDTO(DayOfWeek.TUESDAY, LocalTime.of(10, 0), LocalTime.of(11, 0)));
        service.delete(saved.getId());
        assertFalse(repository.existsById(saved.getId()));
    }

    @Test
    void shouldThrowWhenDeleteNotFound() {
        assertThrows(EntityNotFoundException.class, () -> service.delete("nao-existe"));
    }
}
