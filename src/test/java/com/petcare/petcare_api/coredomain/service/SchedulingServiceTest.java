package com.petcare.petcare_api.coredomain.service;

import com.petcare.petcare_api.application.dto.scheduling.SchedulingRequestDTO;
import com.petcare.petcare_api.coredomain.model.Employee;
import com.petcare.petcare_api.coredomain.model.PetService;
import com.petcare.petcare_api.coredomain.model.scheduling.Scheduling;
import com.petcare.petcare_api.coredomain.model.scheduling.enums.SchedulingStatus;
import com.petcare.petcare_api.coredomain.model.user.User;
import com.petcare.petcare_api.infrastructure.repository.SchedulingRepository;
import com.petcare.petcare_api.infrastructure.repository.UserRepository;
import com.petcare.petcare_api.infrastructure.repository.WorkingPeriodRepository;
import com.petcare.petcare_api.utils.*;
import jakarta.persistence.EntityNotFoundException;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    private WorkingPeriodRepository workingPeriodRepository;

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
        when(emailService.waitingForPickupEmail(anyString(), anyString()))
                .thenReturn("<html>ok</html>");

        LocalDateTime schedulingHour = LocalDateTime.of(2025, 8, 4, 10, 0);
        SchedulingRequestDTO requestDTO = new SchedulingRequestDTO(petService.getId(), schedulingHour);
        Scheduling scheduling = schedulingService.create(requestDTO);

        schedulingService.updateStatus(scheduling.getId(), SchedulingStatus.WAITING_FOR_PICKUP);

        Scheduling updated = schedulingService.findById(scheduling.getId());
        assertEquals(SchedulingStatus.WAITING_FOR_PICKUP, updated.getStatus());

        verify(emailService).waitingForPickupEmail(anyString(), anyString());
        verify(emailService).sendHtml(anyString(), anyString(), eq("<html>ok</html>"));
    }

    @Test
    @WithMockCustomUser(email = CLIENT_EMAIL, cpf = CLIENT_CPF)
    void shouldThrowWhenNoWorkingPeriodForDay() {
        LocalDateTime tuesday = LocalDateTime.of(2025, 8, 5, 10, 0);
        SchedulingRequestDTO dto = new SchedulingRequestDTO(petService.getId(), tuesday);
        assertThrows(EntityNotFoundException.class, () -> schedulingService.create(dto));
    }

    @Test
    @WithMockCustomUser(email = CLIENT_EMAIL, cpf = CLIENT_CPF)
    void shouldNotAllowUpdateWhenStatusIsNotWaitingForArrival() {
        LocalDateTime hour = LocalDateTime.of(2025, 8, 4, 10, 0);
        Scheduling scheduling = schedulingService.create(new SchedulingRequestDTO(petService.getId(), hour));
        schedulingService.updateStatus(scheduling.getId(), SchedulingStatus.IN_PROGRESS);
        SchedulingRequestDTO newDto = new SchedulingRequestDTO(petService.getId(), LocalDateTime.of(2025, 8, 4, 11, 0));
        assertThrows(IllegalStateException.class, () -> schedulingService.update(scheduling.getId(), newDto));
    }

    @Test
    @WithMockCustomUser(email = CLIENT_EMAIL, cpf = CLIENT_CPF)
    void getAvailableTimesShouldThrowIfNoWorkingPeriods() {
        LocalDate sunday = LocalDate.of(2025, 8, 3);
        assertThrows(EntityNotFoundException.class, () -> schedulingService.getAvailableTimes(petService.getId(), sunday));
    }

    @Test
    @WithMockCustomUser(email = CLIENT_EMAIL, cpf = CLIENT_CPF)
    void getAvailableDaysShouldContainMondaysOnlyWhenConfigured() {
        LocalDate monthStart = LocalDate.of(2025, 8, 1);
        List<LocalDate> days = schedulingService.getAvailableDays(petService.getId(), monthStart);
        assertTrue(days.stream().allMatch(d -> d.getDayOfWeek() == DayOfWeek.MONDAY));
        assertTrue(days.stream().findAny().isPresent());
    }

    @Test
    @WithMockCustomUser(email = CLIENT_EMAIL, cpf = CLIENT_CPF)
    void findByDateShouldReturnOnlyThatDay() {
        LocalDate date = LocalDate.of(2025, 8, 4);
        schedulingService.create(new SchedulingRequestDTO(petService.getId(), date.atTime(9, 0)));
        schedulingService.create(new SchedulingRequestDTO(petService.getId(), date.atTime(10, 0)));
        workingPeriodTestFactory.persistWorkingPeriod(DayOfWeek.TUESDAY);
        schedulingService.create(new SchedulingRequestDTO(petService.getId(), LocalDate.of(2025, 8, 5).atTime(9, 0)));
        List<Scheduling> result = schedulingService.findByDate(date);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(s -> s.getSchedulingHour().toLocalDate().equals(date)));
    }

    @Test
    @WithMockCustomUser(email = CLIENT_EMAIL, cpf = CLIENT_CPF)
    void delegateToInvalidEmployeeShouldThrow() {
        LocalDateTime hour = LocalDateTime.of(2025, 8, 4, 10, 0);
        Scheduling scheduling = schedulingService.create(new SchedulingRequestDTO(petService.getId(), hour));
        assertThrows(IllegalArgumentException.class, () -> schedulingService.delegateToUser(scheduling.getId(), "emp-nao-existe"));
    }

    @Test
    @WithMockCustomUser(email = CLIENT_EMAIL, cpf = CLIENT_CPF)
    void shouldAllowSchedulingExactlyOnPeriodEdges() {
        LocalDate date = LocalDate.of(2025, 8, 4);

        var period = workingPeriodRepository.findAllByDayOfWeek(DayOfWeek.MONDAY).get(0);
        int duration = petService.getTime();
        LocalTime firstStart = period.getStartTime();
        LocalTime lastStart  = period.getEndTime().minusMinutes(duration);

        Scheduling s1 = schedulingService.create(
                new SchedulingRequestDTO(petService.getId(), date.atTime(firstStart)));
        assertNotNull(s1.getId());

        Scheduling s2 = schedulingService.create(
                new SchedulingRequestDTO(petService.getId(), date.atTime(lastStart)));
        assertNotNull(s2.getId());
    }


    @Test
    @WithMockCustomUser(email = CLIENT_EMAIL, cpf = CLIENT_CPF)
    void getAvailableDaysShouldIgnoreEntityNotFoundAndContinue() {
        SchedulingService spy = Mockito.spy(schedulingService);

        LocalDate monthStart = LocalDate.of(2025, 8, 1);
        LocalDate monday1 = LocalDate.of(2025, 8, 4);
        LocalDate monday2 = LocalDate.of(2025, 8, 11);

        Mockito.doReturn(List.of(LocalTime.of(10, 0)))
                .when(spy).getAvailableTimes(petService.getId(), monday2);
        Mockito.doThrow(new EntityNotFoundException("sem período"))
                .when(spy).getAvailableTimes(petService.getId(), monday1);

        List<LocalDate> days = spy.getAvailableDays(petService.getId(), monthStart);

        assertTrue(days.contains(monday2));
        assertFalse(days.contains(monday1));
    }

    @Test
    @WithMockCustomUser(email = CLIENT_EMAIL, cpf = CLIENT_CPF)
    void getAvailableDaysShouldSkipDatesWhenUnexpectedErrorOccurs() {
        SchedulingService spy = Mockito.spy(schedulingService);

        LocalDate monthStart = LocalDate.of(2025, 8, 1);
        LocalDate monday1 = LocalDate.of(2025, 8, 4);
        LocalDate monday2 = LocalDate.of(2025, 8, 11);

        Mockito.doThrow(new RuntimeException("boom"))
                .when(spy).getAvailableTimes(petService.getId(), monday1);
        Mockito.doReturn(List.of(LocalTime.of(9, 0)))
                .when(spy).getAvailableTimes(petService.getId(), monday2);

        List<LocalDate> days = spy.getAvailableDays(petService.getId(), monthStart);

        assertTrue(days.contains(monday2));
        assertFalse(days.contains(monday1));
    }

    @Test
    @WithMockCustomUser(email = CLIENT_EMAIL, cpf = CLIENT_CPF)
    void getAvailableTimesReturnsEmptyWhenAllSlotsConflict() {
        LocalDate date = LocalDate.of(2025, 8, 4);
        schedulingService.create(new SchedulingRequestDTO(petService.getId(), date.atTime(9, 0)));
        schedulingService.create(new SchedulingRequestDTO(petService.getId(), date.atTime(9, 30)));
        schedulingService.create(new SchedulingRequestDTO(petService.getId(), date.atTime(10, 0)));
        schedulingService.create(new SchedulingRequestDTO(petService.getId(), date.atTime(10, 30)));

        List<LocalTime> times = schedulingService.getAvailableTimes(petService.getId(), date);
        assertFalse(times.contains(LocalTime.of(9, 0)));
        assertFalse(times.contains(LocalTime.of(9, 30)));
        assertFalse(times.contains(LocalTime.of(10, 0)));
        assertFalse(times.contains(LocalTime.of(10, 30)));
    }

}
