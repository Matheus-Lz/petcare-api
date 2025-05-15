package com.petcare.petcare_api.coredomain.service;

import com.petcare.petcare_api.application.dto.workingPeriod.WorkingPeriodRequestDTO;
import com.petcare.petcare_api.application.dto.workingPeriod.WorkingPeriodResponseDTO;
import com.petcare.petcare_api.coredomain.model.WorkingPeriod;
import com.petcare.petcare_api.infrastructure.repository.WorkingPeriodRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkingPeriodService {

    private final WorkingPeriodRepository repository;

    @Autowired
    public WorkingPeriodService(WorkingPeriodRepository repository) {
        this.repository = repository;
    }

    public WorkingPeriodResponseDTO create(WorkingPeriodRequestDTO dto) {
        WorkingPeriod entity = WorkingPeriod.builder()
                .dayOfWeek(dto.dayOfWeek())
                .startTime(dto.startTime())
                .endTime(dto.endTime())
                .build();

        return toResponse(repository.save(entity));
    }

    public List<WorkingPeriodResponseDTO> findAll() {
        return repository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public WorkingPeriodResponseDTO findById(String id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("WorkingPeriod not found"));
    }

    public void delete(String id) {
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
