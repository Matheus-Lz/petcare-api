package com.petcare.petcare_api.application.dto.petservices;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record UpdatePetServiceRequestDTO(

        @NotEmpty(message = "O nome do serviço é obrigatório")
        String name,

        @NotEmpty(message = "A descrição do serviço é obrigatória")
        String description,

        @NotNull(message = "O preço do serviço é obrigatório")
        @Min(value = 0, message = "O preço do serviço deve ser maior que zero")
        Double price,

        @NotNull(message = "O tempo do serviço é obrigatório")
        @Min(value = 1, message = "O tempo do serviço deve ser maior que 0")
        Integer time
) {
}
