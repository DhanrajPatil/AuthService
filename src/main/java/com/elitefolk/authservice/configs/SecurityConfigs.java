package com.elitefolk.authservice.configs;
import com.elitefolk.authservice.aws.AwsSecretManagerService;
import com.elitefolk.authservice.security.services.CustomAuthenticationProvider;
import com.elitefolk.authservice.security.services.CustomUserDetailsService;
import com.elitefolk.authservice.services.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;

@Configuration
public class SecurityConfigs {

    @Value("${frontend.urls}") String allowedOrigins;

    @Bean
    public SecurityFilterChain getSecurityFilterChain(HttpSecurity http) throws Exception {
        http.
                cors(cors -> cors.configurationSource(getCorsConfigurationSource())).
                csrf(csrf -> csrf.ignoringRequestMatchers("/auth/login",
                        "/auth/signup", "/.well-known/jwks.json", "/auth/google-login",
                        "/otp/generate", "/otp/verify")).
                authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "auth/signup").permitAll()
                        .requestMatchers(HttpMethod.POST,"auth/google-login").permitAll()
                        .requestMatchers(HttpMethod.GET, ".well-known/jwks.json").permitAll()
                        .requestMatchers(HttpMethod.POST, "otp/generate").permitAll()
                        .requestMatchers(HttpMethod.POST, "otp/verify").permitAll()
                        .anyRequest().authenticated()
                ).
                oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }

    @Bean
    public CorsConfigurationSource getCorsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD", "TRACE"));
        corsConfiguration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        corsConfiguration.setExposedHeaders(Arrays.asList("Authorization"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    @Bean
    @Profile("prod")
    public ReloadableJwkSource jwkSource(AwsSecretManagerService awsSecretManagerService) {
        return new ReloadableJwkSource(awsSecretManagerService);
    }

    @Bean
    @Profile("local")
    public ReloadableJwkSource jwkSource(RSAPrivateKey rsaPrivateKey, RSAPublicKey rsaPublicKey) {
        return new ReloadableJwkSource(rsaPrivateKey, rsaPublicKey);
    }

    @Bean
    @Profile("local")
    public AwsSecretManagerService awsSecretManagerService(@Value("${aws.region}") String region,
                                                           @Value("${aws.sm.access-key}") String smAccessKey,
                                                           @Value("${aws.sm.secret-key}") String smSecretKey) {
        return new AwsSecretManagerService(region, smAccessKey, smSecretKey);
    }

    @Bean
    @Profile("prod")
    public AwsSecretManagerService awsSecretManagerService(@Value("${aws.region}") String region) {
        return new AwsSecretManagerService(region);
    }

    @Bean
    public ReloadableJwtEncoder jwtEncoder(ReloadableJwkSource jwkSource) {
        return new ReloadableJwtEncoder(jwkSource);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AwsCredentialsProvider getAwsCredentialsProvider() {
        return DefaultCredentialsProvider.create();
    }

    @Bean
    public UserDetailsService getCustomUserDetailsService(UserService userService) {
        return new CustomUserDetailsService(userService);
    }

    //  when we want default authentication manager which will verify username and password
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
//        return authenticationConfiguration.getAuthenticationManager();
//    }

    // when we need customization for Authentication like, verifying user with OTP or password
    @Bean
    public AuthenticationManager getAuthManager(HttpSecurity http,
                                                CustomAuthenticationProvider authProvider) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                    .authenticationProvider(authProvider)
                    .build();
    }

}
