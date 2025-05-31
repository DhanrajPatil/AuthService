package com.elitefolk.authservice.security.services;

import com.elitefolk.authservice.security.models.CustomUserDetails;
import com.elitefolk.authservice.models.LoginMode;
import com.elitefolk.authservice.security.models.CustomAuthenticationToken;
import com.elitefolk.authservice.services.UserOtpService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private UserOtpService userOtpService;
    private CustomUserDetailsService customUserDetailsService;
    private PasswordEncoder passwordEncoder;

    public CustomAuthenticationProvider(UserOtpService userOtpService, CustomUserDetailsService customUserDetailsService) {
        this.userOtpService = userOtpService;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        CustomAuthenticationToken customAuthenticationToken = (CustomAuthenticationToken) authentication;
        String username = customAuthenticationToken.getName();
        String password = (String) customAuthenticationToken.getCredentials();
        LoginMode loginMode = customAuthenticationToken.getLoginMode();
        if (loginMode == LoginMode.OTP) {
            if(this.userOtpService.verifyOtp(username, Integer.parseInt(password))) {
                // OTP verification successful
                CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(username);
                return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
            } else {
                // OTP verification failed
                throw new AuthenticationException("Invalid username or OTP") {};
            }
        } else if(loginMode == LoginMode.PASSWORD) {
            // you can use the customUserDetailsService to load user details
            // and verify the password.
             CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(username);
             if (userDetails != null && passwordEncoder.matches(password, userDetails.getPassword())) {
                 return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
             } else {
                // Password verification failed
                throw new AuthenticationException("Invalid username or password") {};
             }
        } else {
            throw new AuthenticationException("Unsupported login mode") {};
        }
        // If the login mode is not recognized, throw an exception
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return false;
    }
}
