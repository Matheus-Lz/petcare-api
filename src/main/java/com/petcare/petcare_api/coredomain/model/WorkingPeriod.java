package com.petcare.petcare_api.coredomain.model;

import com.petcare.petcare_api.infrastructure.baseEntities.BaseModel;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(name = "working_period")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Where(clause = "deleted = false")
public class WorkingPeriod extends BaseModel {

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    public void validateInternalState() {
        if (this.dayOfWeek == null) {
            throw new IllegalArgumentException("O dia da semana não pode ser nulo.");
        }
        if (this.startTime == null || this.endTime == null) {
            throw new IllegalArgumentException("Os horários de início e fim não podem ser nulos.");
        }
        if (this.startTime.isAfter(this.endTime) || this.startTime.equals(this.endTime)) {
            throw new IllegalArgumentException("O horário de início (" + this.startTime +
                    ") deve ser anterior ao horário de término (" + this.endTime +
                    ") para " + this.dayOfWeek + ".");
        }
    }
}