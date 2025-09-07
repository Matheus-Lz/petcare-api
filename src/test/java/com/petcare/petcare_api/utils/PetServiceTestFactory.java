package com.petcare.petcare_api.utils;

import com.petcare.petcare_api.application.dto.petservices.CreatePetServiceRequestDTO;
import com.petcare.petcare_api.coredomain.model.PetService;
import com.petcare.petcare_api.infrastructure.repository.PetServiceRepository;
import org.springframework.stereotype.Component;

@Component
public class PetServiceTestFactory {

    private final PetServiceRepository petServiceRepository;

    public PetServiceTestFactory(PetServiceRepository petServiceRepository) {
        this.petServiceRepository = petServiceRepository;
    }

    public static CreatePetServiceRequestDTO buildCreateRequestDTO() {
        return new CreatePetServiceRequestDTO(
                "Banho e Tosa",
                "Serviço completo de banho e tosa para cães de pequeno porte.",
                50.0,
                60
        );
    }

    public static PetService buildEntity() {
        return buildEntityWithTime(60);
    }

    public static PetService buildEntityWithTime(int time) {
        return PetService.builder()
                .name("Banho e Tosa")
                .description("Serviço completo de banho e tosa para cães de pequeno porte.")
                .price(50.0)
                .time(time)
                .build();
    }

    public PetService persistPetService() {
        PetService entity = PetService.builder()
                .name("Banho e Tosa")
                .description("Serviço completo de banho e tosa para cães de pequeno porte.")
                .price(50.0)
                .time(29)
                .build();
        return petServiceRepository.save(entity);
    }
}