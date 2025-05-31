package com.elitefolk.authservice.scheduler;

import com.elitefolk.authservice.configs.ReloadableJwkSource;
import com.elitefolk.authservice.configs.ReloadableJwtEncoder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class KeyRotationRefresher {

    private final ReloadableJwkSource reloadableJwkSource;
    private final ReloadableJwtEncoder reloadableJwtEncoder;

    public KeyRotationRefresher(ReloadableJwtEncoder encoder,
                                ReloadableJwkSource source) {
        reloadableJwkSource = source;
        reloadableJwtEncoder = encoder;
    }

    @Scheduled(fixedDelay = 600_000) // every 10 minutes new keys will be fetched and encoder will be reloaded
    public void reloadJwtBeans() {
        this.reloadableJwkSource.reloadKeys();
        this.reloadableJwtEncoder.reloadEncoder();
    }
}
