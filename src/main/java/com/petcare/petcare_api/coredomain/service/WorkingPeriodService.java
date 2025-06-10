package com.petcare.petcare_api.coredomain.service;

import com.petcare.petcare_api.application.dto.workingPeriod.WorkingPeriodRequestDTO;
import com.petcare.petcare_api.application.dto.workingPeriod.WorkingPeriodResponseDTO;
import com.petcare.petcare_api.coredomain.model.WorkingPeriod;
import com.petcare.petcare_api.infrastructure.repository.WorkingPeriodRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkingPeriodService {

    private final WorkingPeriodRepository repository;

    @Autowired
    public WorkingPeriodService(WorkingPeriodRepository repository) {
        this.repository = repository;
    }

    private void validateNoOverlap(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime, String idToExclude) {
        List<WorkingPeriod> existingPeriods = repository.findAllByDayOfWeek(dayOfWeek);

        for (WorkingPeriod existingPeriod : existingPeriods) {
            if (existingPeriod.getId().equals(idToExclude)) {
                continue;
            }

            boolean overlaps = startTime.isBefore(existingPeriod.getEndTime()) &&
                    endTime.isAfter(existingPeriod.getStartTime());

            if (overlaps) {
                throw new IllegalArgumentException(
                        String.format("Conflito de horário: O período proposto (%s: %s - %s) se sobrepõe com o período existente (%s - %s, ID: %s).",
                                dayOfWeek, startTime, endTime,
                                existingPeriod.getStartTime(), existingPeriod.getEndTime(), existingPeriod.getId())
                );
            }
        }
    }

    public WorkingPeriodResponseDTO create(WorkingPeriodRequestDTO dto) {
        WorkingPeriod entity = WorkingPeriod.builder()
                .dayOfWeek(dto.dayOfWeek())
                .startTime(dto.startTime())
                .endTime(dto.endTime())
                .build();

        entity.validateInternalState();
        validateNoOverlap(dto.dayOfWeek(), dto.startTime(), dto.endTime(), null);

        return toResponse(repository.save(entity));
    }

    public WorkingPeriodResponseDTO update(String id, WorkingPeriodRequestDTO dto) {
        WorkingPeriod entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Período de trabalho com ID " + id + " não encontrado."));

        entity.setDayOfWeek(dto.dayOfWeek());
        entity.setStartTime(dto.startTime());
        entity.setEndTime(dto.endTime());

        entity.validateInternalState();
        validateNoOverlap(dto.dayOfWeek(), dto.startTime(), dto.endTime(), entity.getId());

        return toResponse(repository.save(entity));
    }

    public List<WorkingPeriodResponseDTO> findAll() {
        return repository.findAllByOrderByStartTimeAsc().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public WorkingPeriodResponseDTO findById(String id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Período de trabalho com ID " + id + " não encontrado."));
    }

    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Período de trabalho com ID " + id + " não encontrado para exclusão.");
        }
        repository.deleteById(id);
    }

    private WorkingPeriodResponseDTO toResponse(WorkingPeriod entity) {
        return new WorkingPeriodResponseDTO(
                entity.getId(),
                entity.getDayOfWeek(),
                entity.getStartTime(),
                entity.getEndTime()
        );
    }
}
