package com.petcare.petcare_api.utils;

import com.petcare.petcare_api.application.dto.employee.CreateEmployeeRequestDTO;
import com.petcare.petcare_api.coredomain.model.Employee;
import com.petcare.petcare_api.coredomain.model.PetService;
import com.petcare.petcare_api.coredomain.model.user.User;
import com.petcare.petcare_api.coredomain.model.user.enums.UserRole;
import com.petcare.petcare_api.infrastructure.repository.EmployeeRepository;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
public class EmployeeTestFactory {

    private final EmployeeRepository employeeRepository;
    private final UserTestFactory userTestFactory;
    private final PetServiceTestFactory petServiceTestFactory;

    public EmployeeTestFactory(EmployeeRepository employeeRepository, UserTestFactory userTestFactory, PetServiceTestFactory petServiceTestFactory) {
        this.employeeRepository = employeeRepository;
        this.userTestFactory = userTestFactory;
        this.petServiceTestFactory = petServiceTestFactory;
    }


    public static CreateEmployeeRequestDTO buildCreateRequestDTO() {
        return new CreateEmployeeRequestDTO(
                UserTestFactory.buildRegisterRequest(),
                Collections.singletonList(UUID.randomUUID().toString())
        );
    }

    public static Employee buildEmployee() {
        User user = UserTestFactory.buildUser(UserRole.EMPLOYEE);
        return Employee.builder()
                .user(user)
                .petServiceList(Collections.emptyList())
                .build();
    }

    public Employee persistEmployee(String email, String cpfCnpj) {
        User user = userTestFactory.persistUser(UserRole.EMPLOYEE, email, cpfCnpj);
        PetService petService = petServiceTestFactory.persistPetService();

        Employee employee = Employee.builder()
                .user(user)
                .petServiceList(List.of(petService))
                .build();

        return employeeRepository.save(employee);
    }
}