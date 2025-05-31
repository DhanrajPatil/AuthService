package com.elitefolk.authservice.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
@Profile("local")
/**
 * Configuration class for loading RSA keys from local properties.
 * This class is used when the application is running in a local environment.
 * It provides methods to load RSA public and private keys from the application properties.
 */
public class LocalRsaKeyConfig {

    /**
     * Loads the RSA public key from the application properties.
     *
     * @param publicKey the RSA public key in Base64 format
     * @return the RSAPublicKey object
     */
    @Bean
    @RefreshScope
    public RSAPublicKey getRsaPublicKey(@Value("${jwt.public-key}") String publicKey) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(publicKey);
            keyBytes = new String(keyBytes)
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s+", "").getBytes(StandardCharsets.UTF_8);
            return (RSAPublicKey) KeyFactory.getInstance("RSA")
                    .generatePublic(new X509EncodedKeySpec(keyBytes));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load RSA public key", e);
        }
    }

    /**
     * Loads the RSA private key from the application properties.
     *
     * @param privateKey the RSA private key in Base64 format
     * @return the RSAPrivateKey object
     */
    @Bean
    @RefreshScope
    public RSAPrivateKey getRsaPrivateKey(@Value("${jwt.private-key}") String privateKey) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(privateKey);
            keyBytes = new String(keyBytes)
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "").getBytes(StandardCharsets.UTF_8);
            return (RSAPrivateKey) KeyFactory.getInstance("RSA")
                    .generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load RSA private key", e);
        }
    }
}
