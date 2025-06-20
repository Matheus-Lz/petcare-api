package com.petcare.petcare_api.application.dto.user;

import com.petcare.petcare_api.coredomain.model.user.User;
import lombok.Data;

@Data
public class UserResponse {
    private String email;
    private String cpfCnpj;
    private String name;

    public UserResponse(User user) {
        this.email = user.getEmail();
        this.cpfCnpj = user.getCpfCnpj();
        this.name = user.getName();
    }
}
