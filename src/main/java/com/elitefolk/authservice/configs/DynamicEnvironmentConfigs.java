package com.elitefolk.authservice.configs;

import jakarta.annotation.PostConstruct;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is responsible for dynamically loading environment variables into the Spring environment.
 * It listens for changes in the environment and refreshes the properties accordingly.
 */
@Configuration
public class DynamicEnvironmentConfigs {

    private ConfigurableEnvironment environment;
    private final String DYNAMIC_ENV = "dynamic-env";

    public DynamicEnvironmentConfigs(ConfigurableEnvironment environment) {
        this.environment = environment;
    }

    /**
     * Initializes the dynamic environment variables by loading them from the system environment.
     * This method is called after the bean is constructed.
     */
    @PostConstruct
    public void init() {
        refreshProperties();
        System.out.println("Dynamic environment variables loaded.");
    }

    /**
     * Listens for environment change events and refreshes the properties.
     *
     * @param event the environment change event
     */
    @EventListener(EnvironmentChangeEvent.class)
    public void onEnvironmentChange() {
        refreshProperties();
        System.out.println("Dynamic environment variables refreshed.");
    }

    /**
     * Refreshes the properties from the environment variables and adds them to the Spring environment.
     */
    public void refreshProperties() {
        Map<String, Object> envVars = new HashMap<>();
        System.getenv().forEach((key, value) -> {
            String newKay = key.toLowerCase().replace("_", ".");
            envVars.put(newKay, value);
        });
        PropertySource<?> propertySource = new MapPropertySource(DYNAMIC_ENV, envVars);
        environment.getPropertySources().remove(DYNAMIC_ENV);
        environment.getPropertySources().addFirst(propertySource);
    }
}
