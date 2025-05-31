package com.elitefolk.authservice.configs;

import com.elitefolk.authservice.aws.AwsSecretManagerService;
import com.github.f4b6a3.uuid.UuidCreator;
import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import jakarta.annotation.PostConstruct;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for managing the JWK (JSON Web Key) source.
 * It can reload keys from AWS Secret Manager or use local RSA keys.
 * The class implements JWKSource interface to provide JWKs for JWT processing.
 */
public class ReloadableJwkSource implements JWKSource<SecurityContext> {

    private AwsSecretManagerService awsSecretManagerService;
    private JWKSet jwkSet;
    private RSAKey currentSigningKey;
    private RSAPublicKey rsaPublicKey;
    private RSAPrivateKey rsaPrivateKey;

    /**
     * Constructor for initializing the JWK source with AWS Secret Manager service.
     *
     * @param awsSecretManagerService the AWS Secret Manager service
     */
    public ReloadableJwkSource(AwsSecretManagerService awsSecretManagerService) {
        this.awsSecretManagerService = awsSecretManagerService;
    }

    /**
     * Constructor for initializing the JWK source with local RSA keys.
     *
     * @param rsaPrivateKey the RSA private key
     * @param rsaPublicKey  the RSA public key
     */
    public ReloadableJwkSource(RSAPrivateKey rsaPrivateKey, RSAPublicKey rsaPublicKey) {
        this.rsaPrivateKey = rsaPrivateKey;
        this.rsaPublicKey = rsaPublicKey;
    }

    /**
     * Constructor for initializing the JWK source with a JWKSet.
     *
     * @param jwkSet the JWKSet
     */
    @PostConstruct
    public void init() {
        reloadKeys();
    }

    /**
     * Reloads the keys from AWS Secret Manager or uses local RSA keys.
     * It creates a new JWKSet with the current signing key.
     */
    public void reloadKeys() {
        try {
            if(awsSecretManagerService != null) {
                rsaPublicKey = awsSecretManagerService.getRsaPublicKey();
                rsaPrivateKey = awsSecretManagerService.getRsaPrivateKey();
            }
            this.currentSigningKey = new RSAKey.Builder(rsaPublicKey)
                    .privateKey(rsaPrivateKey)
                    .keyID(UuidCreator.getTimeOrdered().toString())
                    .build();
            List<JWK> jwkList = jwkSet != null ? jwkSet.getKeys() : List.of();
            List<JWK> updatingList = new ArrayList<>(jwkList);
            updatingList.add(currentSigningKey);
            jwkSet = new JWKSet(updatingList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Fetches the current signing key.
     *
     * @return the current signing key
     */
    public RSAKey fetchCurrentSigningKey() {
        return currentSigningKey;
    }

    /**
     * Fetches the current JWKSet.
     * Other microservices can use this method to get the JWKSet for decoding the JWTs.
     *
     * @return the current JWKSet
     */
    @Override
    public List<JWK> get(JWKSelector jwkSelector, SecurityContext securityContext) throws KeySourceException {
        return jwkSelector.select(jwkSet);
    }
}
