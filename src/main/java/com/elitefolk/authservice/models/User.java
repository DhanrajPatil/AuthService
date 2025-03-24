package com.elitefolk.authservice.models;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity(name = "users")
@Table(name = "users")
public class User extends EntityBaseClass{
    @Column(length = 30, nullable = false)
    private String firstName;
    @Column(length = 30, nullable = false)
    private String lastName;

    @Column(length = 50, unique = true)
    private String email;
    @Column(length = 15, unique = true)
    private String mobile;

    @Column(nullable = false)
    private String password;

    private String role;
    private boolean isEmailVerified;
    private boolean isMobileVerified;

}
