package com.elitefolk.authservice.models;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class UserOtp extends EntityBaseClass{
    private String userName;
    private Integer otp;
    private Long expiryTime;
    private Boolean isVerified = false;
}
