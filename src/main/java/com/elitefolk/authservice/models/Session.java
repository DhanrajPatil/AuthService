package com.elitefolk.authservice.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Session extends EntityBaseClass {
    @ManyToOne
    private User user;

    @Column(nullable = false, length = 1500)
    private String token;

    private Long expiryTime;

    private SessionTokenStatus status;

    @Column(length = 32)
    private String deviceId;
    @Column(length = 32)
    private String ipAddress;
    @Column(length = 150)
    private String browser;
}
