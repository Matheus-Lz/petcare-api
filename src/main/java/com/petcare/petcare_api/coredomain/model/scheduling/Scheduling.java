package com.petcare.petcare_api.coredomain.model.scheduling;

import com.petcare.petcare_api.coredomain.model.Employee;
import com.petcare.petcare_api.coredomain.model.PetService;
import com.petcare.petcare_api.coredomain.model.scheduling.enums.SchedulingStatus;
import com.petcare.petcare_api.coredomain.model.user.User;
import com.petcare.petcare_api.infrastructure.baseentities.BaseModel;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Table(name = "scheduling")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Where(clause = "deleted = false")
public class Scheduling extends BaseModel {

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "pet_service_id", nullable = false)
    private PetService petService;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private SchedulingStatus status;

    @Column(name = "scheduling_hour", nullable = false)
    private LocalDateTime schedulingHour;
}
