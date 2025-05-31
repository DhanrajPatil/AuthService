package com.elitefolk.authservice.exceptions;

import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {
    private String details;

    public UserNotFoundException(String message, String details) {
        super(message);
        this.details = details;
    }
}
