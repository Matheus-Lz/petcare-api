package com.petcare.petcare_api.infrastructure.repository;

import com.petcare.petcare_api.coredomain.model.schedulling.Schedulling;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SchedullingRepository extends JpaRepository<Schedulling, String> {
    List<Schedulling> findBySchedullingHourBetween(LocalDateTime start, LocalDateTime end);

    Page<Schedulling> findByUserId(String userId, Pageable pageable);
}
