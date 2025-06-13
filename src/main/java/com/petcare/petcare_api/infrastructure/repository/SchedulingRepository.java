package com.petcare.petcare_api.infrastructure.repository;

import com.petcare.petcare_api.coredomain.model.scheduling.Scheduling;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SchedulingRepository extends JpaRepository<Scheduling, String> {
    List<Scheduling> findBySchedulingHourBetween(LocalDateTime start, LocalDateTime end);

    Page<Scheduling> findByUserId(String userId, Pageable pageable);
}
