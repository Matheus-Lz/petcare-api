package com.petcare.petcare_api.coredomain.model.user;

import com.petcare.petcare_api.coredomain.model.user.enums.UserRole;
import com.petcare.petcare_api.infrastructure.baseEntities.BaseModel;
import com.petcare.petcare_api.infrastructure.converter.UserRoleConverter;
import com.petcare.petcare_api.infrastructure.utils.CpfCnpjUtils;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
@Where(clause = "deleted = false")
public class User extends BaseModel implements UserDetails {

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "cpfCnpj", nullable = false)
    private String cpfCnpj;

    @Convert(converter = UserRoleConverter.class)
    @Column(name = "role", nullable = false)
    private UserRole role;

    @Column(name = "reset_token")
    private String resetToken;

    @Column(name = "reset_token_expiration")
    private LocalDateTime resetTokenExpiration;

    public void validate() {
        if (!StringUtils.hasText(this.email)) {
            throw new IllegalArgumentException("O email não pode ser vazio");
        }

        if (this.password.length() < 6) {
            throw new IllegalArgumentException("A senha deve ter pelo menos 6 caracteres");
        }

        if (!CpfCnpjUtils.validate(cpfCnpj)) {
            throw new IllegalArgumentException("O Cpf/Cnpj informado é inválido");
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == UserRole.USER) return List.of(new SimpleGrantedAuthority("ROLE_USER"));
        if (this.role == UserRole.EMPLOYEE) return List.of(new SimpleGrantedAuthority("ROLE_EMPLOYEE"));
        if (this.role == UserRole.SUPER_ADMIN) return List.of(new SimpleGrantedAuthority("ROLE_SUPER_ADMIN"));

        throw new RuntimeException("User.getAuthorities -> Erro ao consultar permissões do usuário " + this.getId());
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
