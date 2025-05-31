package com.elitefolk.authservice.aws;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Service class for interacting with AWS Secrets Manager.
 * This class provides methods to retrieve RSA public and private keys from AWS Secrets Manager.
 */
public class AwsSecretManagerService {

    private SecretsManagerClient secretsManagerClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String secretName = "auth-service/rsa-keys";

    // Constructor for custom credentials provider
    // This constructor is used when you want to provide your own credentials
    // for example, when you are running the application on a local machine
    public AwsSecretManagerService(String region,
                                   String smAccessKey,
                                   String smSecretKey) {
        try{
            this.secretsManagerClient = SecretsManagerClient.builder()
                    .region(Region.of(region))
                    .credentialsProvider(() -> AwsBasicCredentials.create(smAccessKey, smSecretKey))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Constructor for default credentials provider
    // This constructor is used when the AWS SDK is configured to use the default credentials provider chain
    // which looks for credentials in environment variables, system properties, or the default profile
    public AwsSecretManagerService(String region) {
        try{
            this.secretsManagerClient = SecretsManagerClient.builder()
                    .region(Region.of(region))
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AwsSecretManagerService(SecretsManagerClient secretsManagerClient) {
        this.secretsManagerClient = secretsManagerClient;
    }

    public AwsSecretManagerService() {
        this.secretsManagerClient = SecretsManagerClient.builder()
                .region(Region.of("ap-south-1"))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    /**
     * Retrieves the RSA public key string from AWS Secrets Manager.
     *
     * @return The RSA public key string.
     */
    public String getPublicRsaKeyString(){
        return getSecretField(secretName,"rsa-public-key");
    }

    /**
     * Retrieves the RSA public key from AWS Secrets Manager.
     *
     * @return The RSA public key.
     * @throws Exception If an error occurs while retrieving the key.
     */
    public RSAPublicKey getRsaPublicKey() throws Exception {
        String keyString = getPublicRsaKeyString();
        byte[] keyBytes = Base64.getDecoder().decode(keyString);
        keyBytes = new String(keyBytes)
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "").getBytes(StandardCharsets.UTF_8);
        return (RSAPublicKey) KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(keyBytes));
    }

    /**
     * Retrieves the RSA private key string from AWS Secrets Manager.
     *
     * @return The RSA private key string.
     */
    public String getPrivateRsaKeyString() {
        return getSecretField(secretName,"rsa-private-key");
    }

    /**
     * Retrieves the RSA private key from AWS Secrets Manager.
     *
     * @return The RSA private key.
     * @throws Exception If an error occurs while retrieving the key.
     */
    public RSAPrivateKey getRsaPrivateKey() throws Exception {
        String privateKey = getPrivateRsaKeyString();
        byte[] keyBytes = Base64.getDecoder().decode(privateKey);
        keyBytes = new String(keyBytes)
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "").getBytes(StandardCharsets.UTF_8);
        RSAPrivateKey key = (RSAPrivateKey) KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
        String str = key.getEncoded().toString();
        System.out.println(str.equals(privateKey));
        return key;
    }

    /**
     * Retrieves a specific field from a specific secret in AWS Secrets Manager.
     *
     * @param secretName The name of the secret.
     * @param fieldName  The name of the field to retrieve.
     * @return The value of the specified field from specified secret.
     */
    public String getSecretField(String secretName, String fieldName) {
        GetSecretValueRequest request = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();
        GetSecretValueResponse response = secretsManagerClient.getSecretValue(request);
        try {
            JsonNode secretJson = objectMapper.readTree(response.secretString());
            return secretJson.get(fieldName).asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse secret JSON", e);
        }
    }
}
