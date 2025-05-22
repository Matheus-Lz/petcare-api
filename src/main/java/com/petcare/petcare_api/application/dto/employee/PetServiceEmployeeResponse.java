package com.petcare.petcare_api.application.dto.employee;

import com.petcare.petcare_api.coredomain.model.PetService;

public record PetServiceEmployeeResponse(
        String id,
        String name) {

    public PetServiceEmployeeResponse(PetService petService) {
        this(petService.getId(),
                petService.getName());
    }
}

