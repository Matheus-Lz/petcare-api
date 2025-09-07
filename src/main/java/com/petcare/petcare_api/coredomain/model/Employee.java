package com.petcare.petcare_api.coredomain.model;

import com.petcare.petcare_api.coredomain.model.user.User;
import com.petcare.petcare_api.infrastructure.baseentities.BaseModel;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "employee")
@Where(clause = "deleted = false")
public class Employee extends BaseModel {

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToMany
    @JoinTable(
            name = "employee_services",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private List<PetService> petServiceList;
}
