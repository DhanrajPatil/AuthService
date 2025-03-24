package com.elitefolk.authservice.aws;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;

@Configuration
public class AwsConfigs {

    @Profile("dev")
    @Bean
    public AwsCredentialsProvider devAwsCredentialsProvider() {
        return ProfileCredentialsProvider.create();
    }

    @Profile("prod")
    @Bean
    public AwsCredentialsProvider prodAwsCredentialsProvider() {
        return DefaultCredentialsProvider.create();
    }
}
