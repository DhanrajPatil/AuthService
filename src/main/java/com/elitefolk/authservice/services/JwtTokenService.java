package com.elitefolk.authservice.services;

import com.elitefolk.authservice.configs.ReloadableJwtEncoder;
import com.elitefolk.authservice.security.models.CustomUserDetails;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Primary
public class JwtTokenService implements TokenService{

    private final JwtDecoder jwtDecoder;
    private ReloadableJwtEncoder jwtEncoder;

    public JwtTokenService(ReloadableJwtEncoder jwtEncoder, @Qualifier("jwtDecoder") JwtDecoder jwtDecoder) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    public String generateToken(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        List<String> roles = new ArrayList<>();
        if(authentication.getAuthorities() == null || authentication.getAuthorities().isEmpty()) {
            roles = List.of("User");
        } else {
            roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();
        }
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .subject(userDetails.getEmail())
                .audience(List.of("film-rental-service", "product-service", "auth-service"))
                .claim("name", userDetails.getFirstName() + " " + userDetails.getLastName())
                .claim("roles", roles)
                .claim("mobile", userDetails.getMobile())
                .claim("userId", userDetails.getId())
                .issuedAt(java.time.Instant.now())
                .expiresAt(java.time.Instant.now().plusSeconds(300)) // 5 minutes
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    @Override
    public String getUserIdFromToken(String token) {
        return jwtDecoder.decode(token).getClaim("userId");
    }

    @Override
    public Boolean validateToken(String token, Authentication authentication) {
        return null;
    }

    @Override
    public String refreshToken(String token) {
        return "";
    }

    @Override
    public ResponseCookie embedTokenInCookie(String token) {
        return ResponseCookie.from("access_token", token)
                .httpOnly(true)
                .sameSite("none")
                .maxAge(300)
                .domain("localhost")
                .secure(true)
                .build();
    }
}
