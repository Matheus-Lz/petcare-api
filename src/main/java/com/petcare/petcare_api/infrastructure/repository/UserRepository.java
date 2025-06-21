package com.petcare.petcare_api.infrastructure.repository;

import com.petcare.petcare_api.coredomain.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    UserDetails findByEmail(String email);

    User findUserByEmail(String email);

    Optional<User> findByResetToken(String resetToken);
}
