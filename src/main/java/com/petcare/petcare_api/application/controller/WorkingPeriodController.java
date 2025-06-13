package com.petcare.petcare_api.application.controller;

import com.petcare.petcare_api.application.dto.workingPeriod.WorkingPeriodRequestDTO;
import com.petcare.petcare_api.application.dto.workingPeriod.WorkingPeriodResponseDTO;
import com.petcare.petcare_api.coredomain.model.WorkingPeriod;
import com.petcare.petcare_api.coredomain.service.WorkingPeriodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/working-periods")
public class WorkingPeriodController {

    private final WorkingPeriodService service;

    @Autowired
    public WorkingPeriodController(WorkingPeriodService service) {
        this.service = service;
    }

    @Operation(summary = "Cria um novo período de trabalho")
    @PostMapping
    public ResponseEntity<WorkingPeriodResponseDTO> create(
            @RequestBody WorkingPeriodRequestDTO dto
    ) {
        WorkingPeriod entity = service.create(dto);
        return ResponseEntity.ok(toResponse(entity));
    }

    @Operation(summary = "Lista todos os períodos de trabalho")
    @GetMapping
    public ResponseEntity<List<WorkingPeriodResponseDTO>> findAll() {
        List<WorkingPeriodResponseDTO> list = service.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Busca um período de trabalho pelo ID")
    @GetMapping("/{id}")
    public ResponseEntity<WorkingPeriodResponseDTO> findById(
            @Parameter(description = "ID do período") @PathVariable String id
    ) {
        WorkingPeriod entity = service.findById(id);
        return ResponseEntity.ok(toResponse(entity));
    }

    @Operation(summary = "Atualiza um período de trabalho existente")
    @PutMapping("/{id}")
    public ResponseEntity<WorkingPeriodResponseDTO> update(
            @Parameter(description = "ID do período a ser atualizado") @PathVariable String id,
            @RequestBody WorkingPeriodRequestDTO dto) {
        WorkingPeriod entity = service.update(id, dto);
        return ResponseEntity.ok(toResponse(entity));
    }

    @Operation(summary = "Remove um período de trabalho pelo ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do período") @PathVariable String id
    ) {
        service.delete(id);
        return ResponseEntity.noContent().build();
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
