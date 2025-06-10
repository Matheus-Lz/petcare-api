package com.petcare.petcare_api.infrastructure.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import com.petcare.petcare_api.coredomain.model.user.enums.UserRole;

@Converter(autoApply = true)
public class UserRoleConverter implements AttributeConverter<UserRole, String> {

    @Override
    public String convertToDatabaseColumn(UserRole attribute) {
        return attribute != null ? attribute.getRole() : null;
    }

    @Override
    public UserRole convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;

        for (UserRole role : UserRole.values()) {
            if (role.getRole().equalsIgnoreCase(dbData)) {
                return role;
            }
        }

        throw new IllegalArgumentException("Invalid value for UserRole: " + dbData);
    }
}
