package com.elitefolk.authservice.repositories;

import com.elitefolk.authservice.models.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SessionRepository extends JpaRepository<Session, UUID> {

    List findAllOrderByExpiryTimeInMinutesDesc();
}
