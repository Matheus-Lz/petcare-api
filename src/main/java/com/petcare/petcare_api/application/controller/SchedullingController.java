package com.petcare.petcare_api.application.controller;

import com.petcare.petcare_api.application.dto.schedulling.SchedullingRequestDTO;
import com.petcare.petcare_api.application.dto.schedulling.SchedullingResponseDTO;
import com.petcare.petcare_api.application.dto.schedulling.SchedullingResponseDetailDTO;
import com.petcare.petcare_api.coredomain.service.SchedullingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/schedullings")
@RequiredArgsConstructor
public class SchedullingController {

    private final SchedullingService service;

    @Operation(summary = "Cria um novo agendamento (sem funcionário atribuído)")
    @PostMapping
    public ResponseEntity<SchedullingResponseDTO> create(
            @RequestBody SchedullingRequestDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @Operation(summary = "Busca um agendamento pelo ID")
    @GetMapping("/{id}")
    public ResponseEntity<SchedullingResponseDetailDTO> findById(
            @Parameter(description = "ID do agendamento") @PathVariable String id) {
        SchedullingResponseDetailDTO response = new SchedullingResponseDetailDTO(service.findById(id));
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Deleta um agendamento pelo ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do agendamento") @PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/available-times")
    public ResponseEntity<List<LocalTime>> getAvailableTimes(
            @RequestParam String petServiceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<LocalTime> availableTimes = service.getAvailableTimes(petServiceId, date);
        return ResponseEntity.ok(availableTimes);
    }

    @GetMapping("/available-days")
    public ResponseEntity<List<LocalDate>> getAvailableDays(
            @RequestParam String petServiceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate monthStart) {

        List<LocalDate> availableDays = service.getAvailableDays(petServiceId, monthStart);
        return ResponseEntity.ok(availableDays);
    }

    @Operation(summary = "Busca os agendamentos do usuário logado com paginação")
    @GetMapping("/user")
    public ResponseEntity<Page<SchedullingResponseDetailDTO>> findUserSchedullings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(service.findByCurrentUser(page, size));
    }

}
