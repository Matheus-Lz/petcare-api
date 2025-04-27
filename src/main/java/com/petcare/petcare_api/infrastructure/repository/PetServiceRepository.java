package com.petcare.petcare_api.infrastructure.repository;

import com.petcare.petcare_api.coredomain.model.PetService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PetServiceRepository extends JpaRepository<PetService, String> {
}
