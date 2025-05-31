package com.elitefolk.authservice.models;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity(name = "users")
@Table(
    name = "users",
    indexes = {
        @Index(name = "user_email_index", columnList = "email"),
        @Index(name = "user_mobile_index", columnList = "mobile")
    }
)
@Getter
@Setter
public class User extends EntityBaseClass {
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

    @Column(length = 100, unique = true)
    private String profileUrl;

    @ManyToMany
    private List<Role> roles;

    private boolean isEmailVerified;
    private boolean isMobileVerified;
}
