package com.petcare.petcare_api.application.dto.user;

import com.petcare.petcare_api.coredomain.model.user.User;
import lombok.Data;

@Data
public class UserResponse {
    private String email;
    private String password;
    private String cpfCnpj;
    private String name;

    public UserResponse(User user) {
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.cpfCnpj = user.getCpfCnpj();
        this.name = user.getName();
    }
}
