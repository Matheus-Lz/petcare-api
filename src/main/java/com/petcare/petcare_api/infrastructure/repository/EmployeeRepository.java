package com.petcare.petcare_api.infrastructure.repository;

import com.petcare.petcare_api.coredomain.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {
}
