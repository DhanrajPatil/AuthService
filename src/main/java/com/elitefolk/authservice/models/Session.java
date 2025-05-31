package com.elitefolk.authservice.models;

import jakarta.persistence.*;
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

    private Long expiryTime;
    private float expiryTimeInMinutes;

    @Enumerated(value = EnumType.ORDINAL)
    private SessionTokenStatus status;

    @Column(length = 32)
    private String deviceId;
    @Column(length = 32)
    private String ipAddress;
    @Column(length = 150)
    private String browser;
}
