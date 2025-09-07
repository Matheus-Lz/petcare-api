package com.petcare.petcare_api.coredomain.service;

import com.petcare.petcare_api.application.dto.scheduling.SchedulingRequestDTO;
import com.petcare.petcare_api.coredomain.model.Employee;
import com.petcare.petcare_api.coredomain.model.PetService;
import com.petcare.petcare_api.coredomain.model.scheduling.Scheduling;
import com.petcare.petcare_api.coredomain.model.scheduling.enums.SchedulingStatus;
import com.petcare.petcare_api.coredomain.model.user.User;
import com.petcare.petcare_api.infrastructure.repository.SchedulingRepository;
import com.petcare.petcare_api.infrastructure.repository.UserRepository;
import com.petcare.petcare_api.utils.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
@ExtendWith(SpringExtension.class)
class SchedulingServiceTest {

    @TestConfiguration
    static class SchedulingServiceTestContextConfiguration {
        @Bean
        public EmailService emailService() {
            return Mockito.mock(EmailService.class);
        }
    }

    @Autowired
    private SchedulingService schedulingService;
    @Autowired
    private SchedulingRepository schedulingRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PetServiceTestFactory petServiceTestFactory;
    @Autowired
    private EmployeeTestFactory employeeTestFactory;
    @Autowired
    private WorkingPeriodTestFactory workingPeriodTestFactory;

    @Autowired
    private EmailService emailService;

    private User currentUser;
    private PetService petService;
    private Employee employee;

    private static final String CLIENT_EMAIL = "client.user@example.com";
    private static final String CLIENT_CPF = "78575439042";
    private static final String EMPLOYEE_EMAIL = "employee.user@example.com";
    private static final String EMPLOYEE_CPF = "16933394017";

    @BeforeEach
    void setup() {
        this.currentUser = (User) userRepository.findByEmail(CLIENT_EMAIL);

        this.petService = petServiceTestFactory.persistPetService();
        this.employee = employeeTestFactory.persistEmployee(EMPLOYEE_EMAIL, EMPLOYEE_CPF);
        workingPeriodTestFactory.persistWorkingPeriod(DayOfWeek.MONDAY);
    }

    @Test
    @WithMockCustomUser(email = CLIENT_EMAIL, cpf = CLIENT_CPF)
    void shouldCreateSchedulingSuccessfully() {
        LocalDateTime schedulingHour = LocalDateTime.of(2025, 8, 4, 10, 0);
        SchedulingRequestDTO requestDTO = new SchedulingRequestDTO(petService.getId(), schedulingHour);

        Scheduling savedScheduling = schedulingService.create(requestDTO);

        assertNotNull(savedScheduling.getId());
        assertEquals(SchedulingStatus.WAITING_FOR_ARRIVAL, savedScheduling.getStatus());
        assertEquals(petService.getId(), savedScheduling.getPetService().getId());
        assertEquals(currentUser.getId(), savedScheduling.getUser().getId());
    }

    @Test
    @WithMockCustomUser(email = CLIENT_EMAIL, cpf = CLIENT_CPF)
    void shouldThrowExceptionWhenCreatingSchedulingOutsideWorkingHours() {
        LocalDateTime schedulingHour = LocalDateTime.of(2025, 8, 4, 8, 0);
        SchedulingRequestDTO requestDTO = new SchedulingRequestDTO(petService.getId(), schedulingHour);

        assertThrows(IllegalArgumentException.class, () -> schedulingService.create(requestDTO));
    }

    @Test
    @WithMockCustomUser(email = CLIENT_EMAIL, cpf = CLIENT_CPF)
    void shouldUpdateSchedulingSuccessfully() {
        LocalDateTime initialHour = LocalDateTime.of(2025, 8, 4, 10, 0);
        SchedulingRequestDTO initialDto = new SchedulingRequestDTO(petService.getId(), initialHour);
        Scheduling savedScheduling = schedulingService.create(initialDto);

        LocalDateTime newHour = LocalDateTime.of(2025, 8, 4, 11, 0);
        SchedulingRequestDTO updateDto = new SchedulingRequestDTO(petService.getId(), newHour);

        Scheduling updatedScheduling = schedulingService.update(savedScheduling.getId(), updateDto);

        assertEquals(newHour, updatedScheduling.getSchedulingHour());
    }

    @Test
    @WithMockCustomUser(email = CLIENT_EMAIL, cpf = CLIENT_CPF)
    void shouldDeleteSchedulingSuccessfully() {
        LocalDateTime schedulingHour = LocalDateTime.of(2025, 8, 4, 10, 0);
        SchedulingRequestDTO requestDTO = new SchedulingRequestDTO(petService.getId(), schedulingHour);
        Scheduling savedScheduling = schedulingService.create(requestDTO);

        schedulingService.delete(savedScheduling.getId());

        assertFalse(schedulingRepository.findById(savedScheduling.getId()).isPresent());
    }

    @Test
    @WithMockCustomUser(email = CLIENT_EMAIL, cpf = CLIENT_CPF)
    void shouldGetAvailableTimesForGivenDate() {
        LocalDateTime conflictHour = LocalDateTime.of(2025, 8, 4, 10, 30);
        SchedulingRequestDTO conflictDto = new SchedulingRequestDTO(petService.getId(), conflictHour);
        schedulingService.create(conflictDto);

        LocalDate date = LocalDate.of(2025, 8, 4);
        List<LocalTime> availableTimes = schedulingService.getAvailableTimes(petService.getId(), date);

        assertFalse(availableTimes.contains(LocalTime.of(10, 30)));
        assertTrue(availableTimes.contains(LocalTime.of(9, 0)));
        assertTrue(availableTimes.contains(LocalTime.of(11, 0)));
    }

    @Test
    @WithMockCustomUser(email = CLIENT_EMAIL, cpf = CLIENT_CPF)
    void shouldFindUserSchedulings() {
        LocalDateTime schedulingHour = LocalDateTime.of(2025, 8, 4, 10, 0);
        SchedulingRequestDTO requestDTO = new SchedulingRequestDTO(petService.getId(), schedulingHour);
        schedulingService.create(requestDTO);

        var schedulings = schedulingService.findByCurrentUser(0, 10);
        assertEquals(1, schedulings.getTotalElements());
        assertEquals(currentUser.getId(), schedulings.getContent().getFirst().getUser().getId());
    }

    @Test
    @WithMockCustomUser(email = CLIENT_EMAIL, cpf = CLIENT_CPF)
    void shouldDelegateSchedulingToEmployee() {
        LocalDateTime schedulingHour = LocalDateTime.of(2025, 8, 4, 10, 0);
        SchedulingRequestDTO requestDTO = new SchedulingRequestDTO(petService.getId(), schedulingHour);
        Scheduling scheduling = schedulingService.create(requestDTO);

        schedulingService.delegateToUser(scheduling.getId(), employee.getId());

        Scheduling delegated = schedulingService.findById(scheduling.getId());
        assertNotNull(delegated.getEmployee());
        assertEquals(employee.getId(), delegated.getEmployee().getId());
    }

    @Test
    @WithMockCustomUser(email = CLIENT_EMAIL, cpf = CLIENT_CPF)
    void shouldUpdateStatusAndSendEmailForWaitingForPickupStatus() {
        LocalDateTime schedulingHour = LocalDateTime.of(2025, 8, 4, 10, 0);
        SchedulingRequestDTO requestDTO = new SchedulingRequestDTO(petService.getId(), schedulingHour);
        Scheduling scheduling = schedulingService.create(requestDTO);

        schedulingService.updateStatus(scheduling.getId(), SchedulingStatus.WAITING_FOR_PICKUP);

        Scheduling updated = schedulingService.findById(scheduling.getId());
        assertEquals(SchedulingStatus.WAITING_FOR_PICKUP, updated.getStatus());
    }
}