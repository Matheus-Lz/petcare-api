package com.petcare.petcare_api.coredomain.service;

import com.petcare.petcare_api.application.dto.employee.CreateEmployeeRequestDTO;
import com.petcare.petcare_api.application.dto.employee.UpdateEmployeeRequestDTO;
import com.petcare.petcare_api.application.dto.user.RegisterRequestDTO;
import com.petcare.petcare_api.coredomain.model.Employee;
import com.petcare.petcare_api.coredomain.model.PetService;
import com.petcare.petcare_api.coredomain.model.user.User;
import com.petcare.petcare_api.coredomain.model.user.enums.UserRole;
import com.petcare.petcare_api.infrastructure.repository.EmployeeRepository;
import com.petcare.petcare_api.infrastructure.repository.UserRepository;
import com.petcare.petcare_api.utils.PetServiceTestFactory;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class EmployeeServiceTest {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PetServiceTestFactory petServiceTestFactory;

    private PetService service1;
    private PetService service2;

    @BeforeEach
    void setup() {
        service1 = petServiceTestFactory.persistPetService();

        PetService tempService = PetServiceTestFactory.buildEntity();
        tempService.setName("Tosa Higiênica");
        petServiceRepository.save(tempService);
        service2 = tempService;
    }

    @Autowired
    private com.petcare.petcare_api.infrastructure.repository.PetServiceRepository petServiceRepository;

    @Test
    void shouldCreateEmployeeSuccessfully() {
        RegisterRequestDTO userDto = new RegisterRequestDTO("employee@test.com", "123456", "66277519093", "Funcionario Teste");
        CreateEmployeeRequestDTO employeeDto = new CreateEmployeeRequestDTO(userDto, List.of(service1.getId()));

        Employee createdEmployee = employeeService.create(employeeDto);

        assertNotNull(createdEmployee.getId());
        assertNotNull(createdEmployee.getUser().getId());
        assertEquals(1, createdEmployee.getPetServiceList().size());
        assertEquals(service1.getId(), createdEmployee.getPetServiceList().get(0).getId());

        User createdUser = userRepository.findById(createdEmployee.getUser().getId()).orElseThrow();
        assertEquals(UserRole.EMPLOYEE, createdUser.getRole());
    }

    @Test
    void shouldUpdateEmployeeServices() {
        RegisterRequestDTO userDto = new RegisterRequestDTO("employee2@test.com", "123456", "66277519093", "Funcionario 2");
        CreateEmployeeRequestDTO createDto = new CreateEmployeeRequestDTO(userDto, Collections.singletonList(service1.getId()));
        Employee employee = employeeService.create(createDto);

        UpdateEmployeeRequestDTO updateDto = new UpdateEmployeeRequestDTO(List.of(service1.getId(), service2.getId()));
        Employee updatedEmployee = employeeService.update(employee.getId(), updateDto);

        assertEquals(2, updatedEmployee.getPetServiceList().size());
    }

    @Test
    void shouldSoftDeleteEmployee() {
        RegisterRequestDTO userDto = new RegisterRequestDTO("employee3@test.com", "123456", "66277519093", "Funcionario 3");
        CreateEmployeeRequestDTO createDto = new CreateEmployeeRequestDTO(userDto, Collections.emptyList());
        Employee employee = employeeService.create(createDto);

        employeeService.delete(employee.getId());

        Employee deletedEmployee = employeeRepository.findById(employee.getId())
                .orElseThrow(() -> new AssertionError("Funcionário não encontrado no banco após o soft delete."));

        assertTrue(deletedEmployee.isDeleted());
    }
}