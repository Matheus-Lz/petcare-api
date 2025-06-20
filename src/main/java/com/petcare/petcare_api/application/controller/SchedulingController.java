package com.petcare.petcare_api.application.controller;

import com.petcare.petcare_api.application.dto.scheduling.SchedulingRequestDTO;
import com.petcare.petcare_api.application.dto.scheduling.SchedulingResponseDTO;
import com.petcare.petcare_api.application.dto.scheduling.SchedulingResponseDetailDTO;
import com.petcare.petcare_api.application.dto.scheduling.UpdateSchedulingStatusRequestDTO;
import com.petcare.petcare_api.coredomain.service.SchedulingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/schedulings")
public class SchedulingController {

    private final SchedulingService service;

    @Autowired
    public SchedulingController(SchedulingService service) {
        this.service = service;
    }

    @Operation(summary = "Cria um novo agendamento (sem funcionário atribuído)")
    @PostMapping
    public ResponseEntity<SchedulingResponseDTO> create(
            @RequestBody SchedulingRequestDTO dto) {
        SchedulingResponseDTO responseDTO = new SchedulingResponseDTO(service.create(dto));

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(responseDTO.id())
                .toUri();

        return ResponseEntity.created(location).body(responseDTO);
    }

    @Operation(summary = "Busca um agendamento pelo ID")
    @GetMapping("/{id}")
    public ResponseEntity<SchedulingResponseDetailDTO> findById(
            @Parameter(description = "ID do agendamento") @PathVariable String id) {
        SchedulingResponseDetailDTO response = new SchedulingResponseDetailDTO(service.findById(id));
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
    public ResponseEntity<Page<SchedulingResponseDetailDTO>> findUserSchedulings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<SchedulingResponseDetailDTO> responseDetailDTO = service.findByCurrentUser(page, size).map(SchedulingResponseDetailDTO::new);
        return ResponseEntity.ok(responseDetailDTO);
    }

    @GetMapping("/by-date")
    public ResponseEntity<List<SchedulingResponseDetailDTO>> getSchedulingsByDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        var schedulings = service.findByDate(date);
        var dtos = schedulings.stream()
                .map(SchedulingResponseDetailDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PatchMapping("/{id}/delegate")
    public ResponseEntity<Void> delegateToCurrentUser(@PathVariable String id) {
        service.delegateToUser(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable String id,
                                             @RequestBody UpdateSchedulingStatusRequestDTO request) {
        service.updateStatus(id, request.status());
        return ResponseEntity.noContent().build();
    }

}
