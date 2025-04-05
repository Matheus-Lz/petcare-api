package com.petcare.petcare_api.infrastructure.baseEntities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseModelWithSoftDelete extends BaseModel {

    private boolean deleted = false;
}

