package com.petcare.petcare_api.infrastructure.baseentities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.petcare.petcare_api.coredomain.model.user.User;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Null;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.context.SecurityContextHolder;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseModelWithUser extends BaseModel {

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @Null
    private User user;

    @PrePersist
    @Transactional
    public void setUser() {
        User authenticatedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (authenticatedUser != null) {
            this.user = authenticatedUser;
        }
    }
}

