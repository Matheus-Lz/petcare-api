package com.petcare.petcare_api.infrastructure.repository;

import com.petcare.petcare_api.coredomain.model.Schedulling;
import com.petcare.petcare_api.coredomain.model.WorkingPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SchedullingRepository extends JpaRepository<Schedulling, String> {
    List<Schedulling> findBySchedullingHourBetween(LocalDateTime start, LocalDateTime end);
}
