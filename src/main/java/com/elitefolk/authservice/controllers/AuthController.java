package com.elitefolk.authservice.controllers;

import com.elitefolk.authservice.dtos.LoginSuccessDto;
import com.elitefolk.authservice.dtos.RegisterUserDto;
import com.elitefolk.authservice.security.models.CustomUserDetails;
import com.elitefolk.authservice.models.LoginMode;
import com.elitefolk.authservice.models.User;
import com.elitefolk.authservice.security.models.CustomAuthenticationToken;
import com.elitefolk.authservice.services.TokenService;
import com.elitefolk.authservice.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final TokenService tokenService;

    public AuthController(AuthenticationManager authenticationManager,
                          UserService userService,
                          TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginSuccessDto> login(@RequestHeader("Authorization") String authorization) {
        String[] authParts = this.decodeAuthorizationHeader(authorization);
        String username = authParts[0];
        String password = authParts[1];
        String loginMode = authParts[2];
        // Perform authentication using the authenticationManager
        // Example: authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        // Handle authentication success or failure
        // Return appropriate response
        // For example, return a JWT token or user details
        Authentication authentication = null;
        if(loginMode == null) {
            authentication = authenticationManager.authenticate(
                new CustomAuthenticationToken(username, password, LoginMode.PASSWORD)
            );
        } else {
            authentication = authenticationManager.authenticate(
                new CustomAuthenticationToken(username, password, LoginMode.valueOf(loginMode))
            );
        }
        ResponseEntity<LoginSuccessDto> entity = null;
        if(authentication.isAuthenticated()) {
            // Authentication successful
            // You can return a JWT token or user details here
            String token = this.tokenService.generateToken(authentication);
            ResponseCookie jwtCookie = this.tokenService.embedTokenInCookie(token);
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            LoginSuccessDto dto = LoginSuccessDto.from(userDetails);
            MultiValueMap<String, String> headers = new HttpHeaders();
            headers.add(HttpHeaders.SET_COOKIE, jwtCookie.toString());
            entity = new ResponseEntity<>(dto, headers, HttpStatus.OK);
        } else {
            // Handle authentication failure
            // Return appropriate error response
            entity = new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        return entity;
    }

    @PostMapping("/logout")
    public void logout() {
        // logout logic
    }

    @PostMapping("/register")
    public User register(@RequestHeader String Authorization, @RequestBody @Valid RegisterUserDto registerUserDto) {
        String[] authParts = this.decodeAuthorizationHeader(Authorization);
        String username = authParts[0];
        String password = authParts[1];
        if(username == null || password == null) {
            throw new IllegalArgumentException("Username and password cannot be null");
        }
        if(registerUserDto.getEmail() == null && registerUserDto.getMobile() == null) {
            throw new IllegalArgumentException("Email Or Mobile must be provided");
        }
        if(registerUserDto.getEmail() == null) {
            registerUserDto.setEmail(username);
        } else if(registerUserDto.getMobile() == null) {
            registerUserDto.setMobile(username);
        }
        registerUserDto.setPassword(password);

        return this.userService.registerUser(registerUserDto);
    }

    @PostMapping("/reset-password")
    public void resetPassword(@RequestBody String email) {
        // reset password logic
    }

    public String[] decodeAuthorizationHeader(String authorization) {
        String base64Credentials = authorization.substring("Basic ".length()).trim();
        byte[] decodedBytes = Base64.getDecoder().decode(base64Credentials);
        return new String(decodedBytes).split(":", 3);
    }
}
