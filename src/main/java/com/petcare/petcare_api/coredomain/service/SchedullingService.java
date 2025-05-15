package com.petcare.petcare_api.coredomain.service;

import com.petcare.petcare_api.application.dto.schedulling.SchedullingRequestDTO;
import com.petcare.petcare_api.application.dto.schedulling.SchedullingResponseDTO;
import com.petcare.petcare_api.coredomain.model.*;

import com.petcare.petcare_api.infrastructure.repository.PetServiceRepository;
import com.petcare.petcare_api.infrastructure.repository.SchedullingRepository;
import com.petcare.petcare_api.infrastructure.repository.UserRepository;
import com.petcare.petcare_api.infrastructure.repository.WorkingPeriodRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SchedullingService {

    private final SchedullingRepository schedullingRepository;
    private final UserRepository userRepository;
    private final PetServiceRepository petServiceRepository;
    private final WorkingPeriodRepository workingPeriodRepository;

    @Autowired
    public SchedullingService(SchedullingRepository schedullingRepository, UserRepository userRepository, PetServiceRepository petServiceRepository, WorkingPeriodRepository workingPeriodRepository) {
        this.schedullingRepository = schedullingRepository;
        this.userRepository = userRepository;
        this.petServiceRepository = petServiceRepository;
        this.workingPeriodRepository = workingPeriodRepository;
    }

    public SchedullingResponseDTO create(SchedullingRequestDTO dto) {
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        PetService petService = petServiceRepository.findById(dto.petServiceId())
                .orElseThrow(() -> new EntityNotFoundException("PetService not found"));

        validateSchedullingTime(dto.schedullingHour(), petService);

        Schedulling schedulling = Schedulling.builder()
                .user(user)
                .petService(petService)
                .schedullingHour(dto.schedullingHour())
                .employee(null)
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

    private SchedullingResponseDTO toResponse(Schedulling s) {
        return new SchedullingResponseDTO(
                s.getId(),
                s.getUser().getId(),
                s.getEmployee() != null ? s.getEmployee().getId() : null,
                s.getPetService().getId(),
                s.getSchedullingHour()
        );
    }

    public void validateSchedullingTime(LocalDateTime schedullingHour, PetService petService) {
        DayOfWeek dayOfWeek = schedullingHour.getDayOfWeek();
        WorkingPeriod workingPeriod = workingPeriodRepository.findByDayOfWeek(dayOfWeek)
                .orElseThrow(() -> new EntityNotFoundException("Não foi encontrado periodo de trabalho para o dia selecionado"));

        LocalTime endOfSchedulling = schedullingHour.toLocalTime().plusMinutes(petService.getTime());
        if (endOfSchedulling.isAfter(workingPeriod.getEndTime())) {
            throw new IllegalArgumentException("Agendamento não pode ser feito, o serviço ultrapassa o horário de trabalho.");
        }
    }

}

