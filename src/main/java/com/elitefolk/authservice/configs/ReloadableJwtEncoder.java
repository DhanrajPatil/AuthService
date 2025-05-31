package com.elitefolk.authservice.configs;


import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import jakarta.annotation.PostConstruct;
import org.springframework.security.oauth2.jwt.*;

public class ReloadableJwtEncoder implements JwtEncoder {
    private volatile JwtEncoder delegateJwtEncoder;
    private final ReloadableJwkSource jwkSource;

    public ReloadableJwtEncoder(ReloadableJwkSource newJwkSource) {
        this.jwkSource = newJwkSource;
    }

    @PostConstruct
    public void init() {
        reloadEncoder();
    }

    public void reloadEncoder() {
        RSAKey key = this.jwkSource.fetchCurrentSigningKey();
        JWKSet jwkSet = new JWKSet(key);
        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(jwkSet);
        this.delegateJwtEncoder = new NimbusJwtEncoder(jwkSource);
        System.out.println("✅ JwtEncoder keys reloaded dynamically. Active Key ID: " + key.getKeyID());
    }

    @Override
    public Jwt encode(JwtEncoderParameters parameters) throws JwtEncodingException {
        return this.delegateJwtEncoder.encode(parameters);
    }
}
