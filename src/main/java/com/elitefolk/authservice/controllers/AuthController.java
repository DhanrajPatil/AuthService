package com.elitefolk.authservice.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @PostMapping
    public void login() {
        // login logic
    }

    @PostMapping("/logout")
    public void logout() {
        // logout logic
    }

    @PostMapping("/register")
    public void register() {
        // register logic
    }
}
