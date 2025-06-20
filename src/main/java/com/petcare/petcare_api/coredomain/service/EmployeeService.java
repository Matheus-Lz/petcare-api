package com.petcare.petcare_api.coredomain.service;

import com.petcare.petcare_api.application.dto.employee.CreateEmployeeRequestDTO;
import com.petcare.petcare_api.application.dto.employee.UpdateEmployeeRequestDTO;
import com.petcare.petcare_api.coredomain.model.Employee;
import com.petcare.petcare_api.coredomain.model.PetService;
import com.petcare.petcare_api.coredomain.model.user.User;
import com.petcare.petcare_api.coredomain.model.user.enums.UserRole;
import com.petcare.petcare_api.infrastructure.repository.EmployeeRepository;
import com.petcare.petcare_api.infrastructure.repository.PetServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository repository;
    private final PetServiceRepository petServiceRepository;
    private final UserService userService;

    @Autowired
    public EmployeeService(EmployeeRepository repository,
                           PetServiceRepository petServiceRepository,
                           UserService userService) {
        this.repository = repository;
        this.petServiceRepository = petServiceRepository;
        this.userService = userService;
    }

    public Employee create(CreateEmployeeRequestDTO dto) {
        User user = userService.registerUser(dto.user(), UserRole.EMPLOYEE);
        List<PetService> services = petServiceRepository.findAllById(dto.serviceIds());

        Employee employee = Employee.builder()
                .user(user)
                .petServiceList(services)
                .build();

        return repository.save(employee);
    }

    public Employee update(String id, UpdateEmployeeRequestDTO dto) {
        Employee employee = getById(id);
        List<PetService> services = petServiceRepository.findAllById(dto.serviceIds());
        employee.setPetServiceList(services);
        return repository.save(employee);
    }

    public Employee getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado"));
    }

    public Employee getByUserId(String userId) {
        return repository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado"));
    }

    public Page<Employee> list(Integer page, Integer size) {
        return repository.findAll(PageRequest.of(page, size));
    }

    public void delete(String id) {
        Employee employee = getById(id);
        employee.setDeleted(true);
        repository.save(employee);
    }
}
