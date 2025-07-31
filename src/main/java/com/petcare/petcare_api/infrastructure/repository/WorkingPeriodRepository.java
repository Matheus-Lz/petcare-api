package com.petcare.petcare_api.infrastructure.repository;

import com.petcare.petcare_api.coredomain.model.WorkingPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;

@Repository
public interface WorkingPeriodRepository extends JpaRepository<WorkingPeriod, String> {

    List<WorkingPeriod> findAllByOrderByStartTimeAsc();

    List<WorkingPeriod> findAllByDayOfWeek(DayOfWeek dayOfWeek);
}
