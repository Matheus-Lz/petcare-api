package com.petcare.petcare_api.infrastructure.repository;

import com.petcare.petcare_api.coredomain.model.PetService;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetServiceRepository extends JpaRepository<PetService, String> {
}
