package com.petcare.petcare_api.application.dto.petservices;

import com.petcare.petcare_api.coredomain.model.PetService;

public record PetServiceResponseDTO(
        String id,
        String name,
        String description,
        Double price,
        Integer time
) {
    public PetServiceResponseDTO(PetService petService) {
        this(petService.getId(),
                petService.getName(), petService.getDescription(),
                petService.getPrice(), petService.getTime());
    }
}
