package com.elitefolk.authservice.services;

import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;

public interface TokenService {
    String generateToken(Authentication authentication);
    String getUserIdFromToken(String token);
    Boolean validateToken(String token, Authentication authentication);
    String refreshToken(String token);
    ResponseCookie embedTokenInCookie(String token);
}
