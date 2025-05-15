package com.petcare.petcare_api.application.controller;

import com.petcare.petcare_api.application.dto.workingPeriod.WorkingPeriodRequestDTO;
import com.petcare.petcare_api.application.dto.workingPeriod.WorkingPeriodResponseDTO;
import com.petcare.petcare_api.coredomain.service.WorkingPeriodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/working-periods")
@RequiredArgsConstructor
public class WorkingPeriodController {

    private final WorkingPeriodService service;

    @Operation(summary = "Cria um novo período de trabalho")
    @PostMapping
    public ResponseEntity<WorkingPeriodResponseDTO> create(
            @RequestBody WorkingPeriodRequestDTO dto
    ) {
        return ResponseEntity.ok(service.create(dto));
    }

    @Operation(summary = "Lista todos os períodos de trabalho")
    @GetMapping
    public ResponseEntity<List<WorkingPeriodResponseDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @Operation(summary = "Busca um período de trabalho pelo ID")
    @GetMapping("/{id}")
    public ResponseEntity<WorkingPeriodResponseDTO> findById(
            @Parameter(description = "ID do período") @PathVariable String id
    ) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Remove um período de trabalho pelo ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do período") @PathVariable String id
    ) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
