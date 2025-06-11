package com.petcare.petcare_api.coredomain.service;

import com.petcare.petcare_api.application.dto.schedulling.SchedullingRequestDTO;
import com.petcare.petcare_api.application.dto.schedulling.SchedullingResponseDTO;
import com.petcare.petcare_api.coredomain.model.*;

import com.petcare.petcare_api.coredomain.model.schedulling.Schedulling;
import com.petcare.petcare_api.coredomain.model.schedulling.enums.SchedullingStatus;
import com.petcare.petcare_api.infrastructure.repository.PetServiceRepository;
import com.petcare.petcare_api.infrastructure.repository.SchedullingRepository;
import com.petcare.petcare_api.infrastructure.repository.WorkingPeriodRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SchedullingService {

    private final SchedullingRepository schedullingRepository;
    private final UserService userService;
    private final PetServiceRepository petServiceRepository;
    private final WorkingPeriodRepository workingPeriodRepository;

    @Autowired
    public SchedullingService(SchedullingRepository schedullingRepository, UserService userService, PetServiceRepository petServiceRepository, WorkingPeriodRepository workingPeriodRepository) {
        this.schedullingRepository = schedullingRepository;
        this.userService = userService;
        this.petServiceRepository = petServiceRepository;
        this.workingPeriodRepository = workingPeriodRepository;
    }

    public SchedullingResponseDTO create(SchedullingRequestDTO dto) {
        PetService petService = petServiceRepository.findById(dto.petServiceId())
                .orElseThrow(() -> new EntityNotFoundException("PetService not found"));

        validateSchedullingTime(dto.schedullingHour(), petService);

        Schedulling schedulling = Schedulling.builder()
                .user(userService.getCurrentUser())
                .petService(petService)
                .schedullingHour(dto.schedullingHour())
                .employee(null)
                .status(SchedullingStatus.WAITING_FOR_ARRIVAL)
                .build();

        return toResponse(schedullingRepository.save(schedulling));
    }

    public List<SchedullingResponseDTO> findAll() {
        return schedullingRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public SchedullingResponseDTO findById(String id) {
        return schedullingRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Schedulling not found"));
    }

    public void delete(String id) {
        schedullingRepository.deleteById(id);
    }

    private SchedullingResponseDTO toResponse(Schedulling schedulling) {
        return new SchedullingResponseDTO(
                schedulling.getId(),
                schedulling.getUser().getId(),
                schedulling.getEmployee() != null ? schedulling.getEmployee().getId() : null,
                schedulling.getPetService().getId(),
                schedulling.getSchedullingHour(),
                schedulling.getStatus()
        );
    }

    public void validateSchedullingTime(LocalDateTime schedullingHour, PetService petService) {
        DayOfWeek dayOfWeek = schedullingHour.getDayOfWeek();
        List<WorkingPeriod> workingPeriods = workingPeriodRepository.findAllByDayOfWeek(dayOfWeek);

        if (workingPeriods.isEmpty()) {
            throw new EntityNotFoundException("Não foi encontrado período de trabalho para o dia selecionado");
        }

        LocalTime start = schedullingHour.toLocalTime();
        LocalTime end = start.plusMinutes(petService.getTime());

        boolean fitsInAnyPeriod = workingPeriods.stream().anyMatch(period ->
                !start.isBefore(period.getStartTime()) && !end.isAfter(period.getEndTime())
        );

        if (!fitsInAnyPeriod) {
            throw new IllegalArgumentException("Agendamento não pode ser feito, o serviço não se encaixa em nenhum período de trabalho.");
        }
    }


    public List<LocalTime> getAvailableTimes(String petServiceId, LocalDate date) {
        PetService petService = petServiceRepository.findById(petServiceId)
                .orElseThrow(() -> new EntityNotFoundException("PetService not found"));

        DayOfWeek dayOfWeek = date.getDayOfWeek();
        List<WorkingPeriod> workingPeriods = workingPeriodRepository.findAllByDayOfWeek(dayOfWeek);

        if (workingPeriods.isEmpty()) {
            throw new EntityNotFoundException("No working periods found for this day");
        }

        LocalDateTime startOfDay = date.atTime(0, 0);
        LocalDateTime endOfDay = date.atTime(23, 59);
        List<Schedulling> schedullings = schedullingRepository.findBySchedullingHourBetween(startOfDay, endOfDay);

        int duration = petService.getTime();
        List<LocalTime> availableTimes = new ArrayList<>();

        for (WorkingPeriod period : workingPeriods) {
            LocalTime cursor = period.getStartTime();

            while (!cursor.plusMinutes(duration).isAfter(period.getEndTime())) {
                LocalTime finalCursor = cursor;

                boolean conflicts = schedullings.stream().anyMatch(s ->
                        !s.getSchedullingHour().toLocalTime().isAfter(finalCursor.plusMinutes(duration).minusSeconds(1)) &&
                                !s.getSchedullingHour().toLocalTime().plusMinutes(s.getPetService().getTime()).isBefore(finalCursor)
                );

                if (!conflicts) {
                    availableTimes.add(cursor);
                }

                cursor = cursor.plusMinutes(30);
            }
        }

        return availableTimes;
    }

    public List<LocalDate> getAvailableDays(String petServiceId, LocalDate monthStart) {
        petServiceRepository.findById(petServiceId)
                .orElseThrow(() -> new EntityNotFoundException("Serviço não encontrado"));

        LocalDate monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth());
        List<LocalDate> availableDays = new ArrayList<>();

        for (LocalDate date = monthStart; !date.isAfter(monthEnd); date = date.plusDays(1)) {
            try {
                List<LocalTime> times = getAvailableTimes(petServiceId, date);
                if (!times.isEmpty()) {
                    availableDays.add(date);
                }
            } catch (EntityNotFoundException ignored) {
            } catch (Exception e) {
                System.err.printf("Erro ao verificar horários disponíveis para o dia %s: %s%n", date, e.getMessage());
            }
        }

        return availableDays;
    }
}

