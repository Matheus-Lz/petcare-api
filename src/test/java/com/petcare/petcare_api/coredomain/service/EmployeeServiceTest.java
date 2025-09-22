package com.petcare.petcare_api.coredomain.service;

import com.petcare.petcare_api.application.dto.employee.CreateEmployeeRequestDTO;
import com.petcare.petcare_api.application.dto.employee.UpdateEmployeeRequestDTO;
import com.petcare.petcare_api.application.dto.user.RegisterRequestDTO;
import com.petcare.petcare_api.coredomain.model.Employee;
import com.petcare.petcare_api.coredomain.model.PetService;
import com.petcare.petcare_api.coredomain.model.user.User;
import com.petcare.petcare_api.coredomain.model.user.enums.UserRole;
import com.petcare.petcare_api.infrastructure.repository.EmployeeRepository;
import com.petcare.petcare_api.infrastructure.repository.PetServiceRepository;
import com.petcare.petcare_api.infrastructure.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class EmployeeServiceTest {

    @Autowired private EmployeeService employeeService;
    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private PetServiceRepository petServiceRepository;
    @Autowired private UserRepository userRepository;

    @MockitoBean
    private UserService userService;

    private PetService service1;
    private PetService service2;

    @BeforeEach
    void setup() {
        service1 = petServiceRepository.save(PetService.builder().name("Banho").description("Desc").price(50.0).time(30).build());
        service2 = petServiceRepository.save(PetService.builder().name("Tosa").description("Desc").price(70.0).time(45).build());

        when(userService.registerUser(any(RegisterRequestDTO.class), any(UserRole.class))).thenAnswer(inv -> {
            RegisterRequestDTO r = inv.getArgument(0);
            User u = User.builder()
                    .email(r.email())
                    .password("secret123")
                    .name(r.name())
                    .cpfCnpj(r.cpfCnpj())
                    .role(UserRole.EMPLOYEE)
                    .build();
            return userRepository.save(u);
        });
    }

    @Test
    void shouldCreateEmployeeSuccessfully() {
        RegisterRequestDTO userDto = new RegisterRequestDTO("employee@test.com", "123456", "Funcionario Teste", "66277519093");
        CreateEmployeeRequestDTO employeeDto = new CreateEmployeeRequestDTO(userDto, List.of(service1.getId()));
        Employee created = employeeService.create(employeeDto);
        assertNotNull(created.getId());
        assertNotNull(created.getUser());
        assertEquals(UserRole.EMPLOYEE, created.getUser().getRole());
        assertEquals(1, created.getPetServiceList().size());
        assertEquals(service1.getId(), created.getPetServiceList().get(0).getId());
    }

    @Test
    void shouldCreateEmployeeWithEmptyServices() {
        RegisterRequestDTO userDto = new RegisterRequestDTO("employee2@test.com", "123456", "Funcionario 2", "66277519093");
        CreateEmployeeRequestDTO employeeDto = new CreateEmployeeRequestDTO(userDto, Collections.emptyList());
        Employee created = employeeService.create(employeeDto);
        assertNotNull(created.getId());
        assertTrue(created.getPetServiceList().isEmpty());
    }

    @Test
    void shouldUpdateEmployeeServices() {
        RegisterRequestDTO userDto = new RegisterRequestDTO("employee3@test.com", "123456", "Funcionario 3", "66277519093");
        CreateEmployeeRequestDTO createDto = new CreateEmployeeRequestDTO(userDto, List.of(service1.getId()));
        Employee employee = employeeService.create(createDto);
        UpdateEmployeeRequestDTO updateDto = new UpdateEmployeeRequestDTO(List.of(service1.getId(), service2.getId()));
        Employee updated = employeeService.update(employee.getId(), updateDto);
        assertEquals(2, updated.getPetServiceList().size());
    }

    @Test
    void shouldUpdateToEmptyServices() {
        RegisterRequestDTO userDto = new RegisterRequestDTO("employee4@test.com", "123456", "Funcionario 4", "66277519093");
        CreateEmployeeRequestDTO createDto = new CreateEmployeeRequestDTO(userDto, List.of(service1.getId(), service2.getId()));
        Employee employee = employeeService.create(createDto);
        UpdateEmployeeRequestDTO updateDto = new UpdateEmployeeRequestDTO(Collections.emptyList());
        Employee updated = employeeService.update(employee.getId(), updateDto);
        assertTrue(updated.getPetServiceList().isEmpty());
    }

    @Test
    void shouldGetById() {
        RegisterRequestDTO userDto = new RegisterRequestDTO("employee5@test.com", "123456", "Funcionario 5", "66277519093");
        CreateEmployeeRequestDTO createDto = new CreateEmployeeRequestDTO(userDto, List.of(service1.getId()));
        Employee employee = employeeService.create(createDto);
        Employee found = employeeService.getById(employee.getId());
        assertEquals(employee.getId(), found.getId());
    }

    @Test
    void shouldThrowWhenGetByIdNotFound() {
        assertThrows(IllegalArgumentException.class, () -> employeeService.getById("nao-existe"));
    }

    @Test
    void shouldListEmployees() {
        RegisterRequestDTO u1 = new RegisterRequestDTO("l1@test.com", "123456", "L1", "66277519093");
        RegisterRequestDTO u2 = new RegisterRequestDTO("l2@test.com", "123456", "L2", "66277519093");
        employeeService.create(new CreateEmployeeRequestDTO(u1, List.of(service1.getId())));
        employeeService.create(new CreateEmployeeRequestDTO(u2, List.of(service2.getId())));
        Page<Employee> page = employeeService.list(0, 10);
        assertTrue(page.getTotalElements() >= 2);
    }

    @Test
    void shouldSoftDeleteEmployee() {
        RegisterRequestDTO userDto = new RegisterRequestDTO("employee6@test.com", "123456", "Funcionario 6", "66277519093");
        CreateEmployeeRequestDTO createDto = new CreateEmployeeRequestDTO(userDto, List.of(service1.getId()));
        Employee employee = employeeService.create(createDto);
        employeeService.delete(employee.getId());
        Employee deleted = employeeRepository.findById(employee.getId()).orElseThrow();
        assertTrue(deleted.isDeleted());
    }

    @Test
    void shouldThrowOnDeleteNotFound() {
        assertThrows(IllegalArgumentException.class, () -> employeeService.delete("nao-existe"));
    }
}
