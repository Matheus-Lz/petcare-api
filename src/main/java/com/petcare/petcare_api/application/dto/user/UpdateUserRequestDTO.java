package com.petcare.petcare_api.application.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateUserRequestDTO(

        @NotNull(message = "O email é obrigatório")
        @Email(message = "O email deve ser válido")
        String email,

        @NotNull(message = "A senha é obrigatória")
        @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres")
        String password,

        @NotNull(message = "O cpfCnpj é obrigatória")
        @Size(max = 14, message = "O cpfCnpj deve ter no máximo 14 caracteres")
        String cpfCnpj,

        @NotNull(message = "O nome é obrigatória")
        String name
) {}

