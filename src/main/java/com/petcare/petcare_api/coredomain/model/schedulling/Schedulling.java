package com.petcare.petcare_api.coredomain.model.schedulling;

import com.petcare.petcare_api.coredomain.model.Employee;
import com.petcare.petcare_api.coredomain.model.PetService;
import com.petcare.petcare_api.coredomain.model.schedulling.enums.SchedullingStatus;
import com.petcare.petcare_api.coredomain.model.user.User;
import com.petcare.petcare_api.infrastructure.baseEntities.BaseModel;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Table(name = "schedulling")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Where(clause = "deleted = false")
public class Schedulling extends BaseModel {

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
    private SchedullingStatus status;

    @Column(name = "schedulling_hour", nullable = false)
    private LocalDateTime schedullingHour;
}
