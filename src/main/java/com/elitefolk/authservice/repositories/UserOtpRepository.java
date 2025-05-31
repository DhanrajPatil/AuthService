package com.elitefolk.authservice.repositories;

import com.elitefolk.authservice.models.UserOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserOtpRepository extends JpaRepository<UserOtp, UUID> {
    Optional<UserOtp> findByUserNameAndIsVerified(String userName, Boolean isVerified);
}
