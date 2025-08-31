package com.petcare.petcare_api.coredomain.service;

import com.petcare.petcare_api.application.dto.scheduling.SchedulingRequestDTO;
import com.petcare.petcare_api.coredomain.model.*;

import com.petcare.petcare_api.coredomain.model.scheduling.Scheduling;
import com.petcare.petcare_api.coredomain.model.scheduling.enums.SchedulingStatus;
import com.petcare.petcare_api.infrastructure.repository.SchedulingRepository;
import com.petcare.petcare_api.infrastructure.repository.WorkingPeriodRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SchedulingService {

    private final SchedulingRepository repository;
    private final UserService userService;
    private final PetServiceService petServiceService;
    private final WorkingPeriodRepository workingPeriodRepository;
    private final EmployeeService employeeService;
    private final EmailService emailService;

    private static final Logger logger = LoggerFactory.getLogger(SchedulingService.class);

    @Autowired
    public SchedulingService(SchedulingRepository repository, UserService userService, PetServiceService petServiceService, WorkingPeriodRepository workingPeriodRepository, EmployeeService employeeService, EmailService emailService) {
        this.repository = repository;
        this.userService = userService;
        this.petServiceService = petServiceService;
        this.workingPeriodRepository = workingPeriodRepository;
        this.employeeService = employeeService;
        this.emailService = emailService;
    }

    public Scheduling create(SchedulingRequestDTO dto) {
        PetService petService = petServiceService.getById(dto.petServiceId());

        validateSchedulingTime(dto.schedulingHour(), petService);

        Scheduling scheduling = Scheduling.builder()
                .user(userService.getCurrentUser())
                .petService(petService)
                .schedulingHour(dto.schedulingHour())
                .employee(null)
                .status(SchedulingStatus.WAITING_FOR_ARRIVAL)
                .build();

        return repository.save(scheduling);
    }

    public Scheduling update(String id, SchedulingRequestDTO dto) {
        Scheduling scheduling = this.findById(id);

        if (scheduling.getStatus() != SchedulingStatus.WAITING_FOR_ARRIVAL) {
            throw new IllegalStateException("Agendamentos só podem ser editados se estiverem aguardando chegada");
        }

        PetService petService = petServiceService.getById(dto.petServiceId());

        validateSchedulingTime(dto.schedulingHour(), petService);

        scheduling.setPetService(petService);
        scheduling.setSchedulingHour(dto.schedulingHour());

        return repository.save(scheduling);
    }

    public Scheduling findById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Agendamento não encontrado"));
    }

    public void delete(String id) {
        repository.deleteById(id);
    }

    public void validateSchedulingTime(LocalDateTime schedulingHour, PetService petService) {
        DayOfWeek dayOfWeek = schedulingHour.getDayOfWeek();
        List<WorkingPeriod> workingPeriods = workingPeriodRepository.findAllByDayOfWeek(dayOfWeek);

        if (workingPeriods.isEmpty()) {
            throw new EntityNotFoundException("Não foi encontrado período de trabalho para o dia selecionado");
        }

        LocalTime start = schedulingHour.toLocalTime();
        LocalTime end = start.plusMinutes(petService.getTime());

        boolean fitsInAnyPeriod = workingPeriods.stream().anyMatch(period ->
                !start.isBefore(period.getStartTime()) && !end.isAfter(period.getEndTime())
        );

        if (!fitsInAnyPeriod) {
            throw new IllegalArgumentException("Agendamento não pode ser feito, o serviço não se encaixa em nenhum período de trabalho.");
        }
    }


    public List<LocalTime> getAvailableTimes(String petServiceId, LocalDate date) {
        PetService petService = petServiceService.getById(petServiceId);

        DayOfWeek dayOfWeek = date.getDayOfWeek();
        List<WorkingPeriod> workingPeriods = workingPeriodRepository.findAllByDayOfWeek(dayOfWeek);

        if (workingPeriods.isEmpty()) {
            throw new EntityNotFoundException("No working periods found for this day");
        }

        LocalDateTime startOfDay = date.atTime(0, 0);
        LocalDateTime endOfDay = date.atTime(23, 59);
        List<Scheduling> schedulings = repository.findBySchedulingHourBetween(startOfDay, endOfDay);

        int duration = petService.getTime();
        List<LocalTime> availableTimes = new ArrayList<>();

        for (WorkingPeriod period : workingPeriods) {
            LocalTime cursor = period.getStartTime();

            while (!cursor.plusMinutes(duration).isAfter(period.getEndTime())) {
                LocalTime finalCursor = cursor;

                boolean conflicts = schedulings.stream().anyMatch(s ->
                        !s.getSchedulingHour().toLocalTime().isAfter(finalCursor.plusMinutes(duration).minusSeconds(1)) &&
                                !s.getSchedulingHour().toLocalTime().plusMinutes(s.getPetService().getTime()).isBefore(finalCursor)
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
        petServiceService.getById(petServiceId);

        LocalDate monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth());
        List<LocalDate> availableDays = new ArrayList<>();

        for (LocalDate date = monthStart; !date.isAfter(monthEnd); date = date.plusDays(1)) {
            try {
                List<LocalTime> times = getAvailableTimes(petServiceId, date);
                if (!times.isEmpty()) {
                    availableDays.add(date);
                }
            } catch (EntityNotFoundException ignored) {
                // Intencionalmente ignorado: ausência de entidade significa que não há horários disponíveis
            } catch (Exception e) {
                logger.error("Erro ao verificar horários disponíveis para o dia {}: {}", date, e.getMessage(), e);
            }
        }

        return availableDays;
    }

    public Page<Scheduling> findByCurrentUser(int page, int size) {
        var user = userService.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateCreated").descending());

        return repository.findByUserId(user.getId(), pageable);
    }

    public List<Scheduling> findByDate(LocalDate date) {
        var start = date.atStartOfDay();
        var end = date.plusDays(1).atStartOfDay();
        return repository.findBySchedulingHourBetween(start, end);
    }

    public void delegateToUser(String schedulingId, String employeeId) {
        Scheduling scheduling = this.findById(schedulingId);

        Employee employee = employeeService.getById(employeeId);

        scheduling.setEmployee(employee);
        repository.save(scheduling);
    }

    public void updateStatus(String schedulingId, SchedulingStatus status) {
        Scheduling scheduling = this.findById(schedulingId);

        scheduling.setStatus(status);
        repository.save(scheduling);

        if (status == SchedulingStatus.WAITING_FOR_PICKUP) {
            String email = scheduling.getUser().getEmail();
            String name = scheduling.getUser().getName();
            String serviceName = scheduling.getPetService().getName();

            String html = emailService.waitingForPickupEmail(name, serviceName);
            emailService.sendHtml(email, "Seu pet está pronto para ser retirado!", html);
        }
    }
}

