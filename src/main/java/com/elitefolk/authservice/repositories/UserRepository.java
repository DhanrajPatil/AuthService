package com.elitefolk.authservice.repositories;

import com.elitefolk.authservice.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    Optional<User> findByMobile(String mobileNumber);

    Optional<User> findByEmailOrMobile(String email, String mobileNumber);

    Boolean existsByEmail(String email);

    Boolean existsByMobile(String mobileNumber);
}
