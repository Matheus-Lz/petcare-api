package com.petcare.petcare_api.infrastructure.repository;

import com.petcare.petcare_api.coredomain.model.PetService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PetServiceRepository extends JpaRepository<PetService, String> {
    @Query("SELECT p FROM PetService p WHERE p.deleted = false")
    Page<PetService> findAllActive(Pageable pageable);
}
