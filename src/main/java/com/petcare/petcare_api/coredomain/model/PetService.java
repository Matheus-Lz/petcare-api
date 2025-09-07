package com.petcare.petcare_api.coredomain.model;

import com.petcare.petcare_api.infrastructure.baseentities.BaseModel;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.StringUtils;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "pet_service")
public class PetService extends BaseModel {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "time", nullable = false)
    private Integer time;

    public void validate() {
        if (!StringUtils.hasText(this.name)) {
            throw new IllegalArgumentException("O nome do serviço não pode ser vazio");
        }

        if (!StringUtils.hasText(this.description)) {
            throw new IllegalArgumentException("A descrição do serviço não pode ser vazia");
        }

        if (this.price == null || this.price <= 0) {
            throw new IllegalArgumentException("O preço do serviço deve ser maior que zero");
        }

        if (this.time == null || this.time <= 0) {
            throw new IllegalArgumentException("O tempo do serviço deve ser maior que zero");
        }
    }
}
