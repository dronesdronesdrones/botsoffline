package com.botsoffline.eve.service;

import javax.annotation.PostConstruct;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
//@Profile(JHipsterConstants.SPRING_PROFILE_PRODUCTION)
public class TriggerMachine {

    private final SystemStatsLoader systemStatsLoader;
    private final CharacterLocationLoader playerStatsLoader;

    public TriggerMachine(final SystemStatsLoader systemStatsLoader,
            final CharacterLocationLoader playerStatsLoader) {
        this.systemStatsLoader = systemStatsLoader;
        this.playerStatsLoader = playerStatsLoader;
    }

    @PostConstruct
    public void init() {
        loadSystemStats();
    }

    @Async
    @Scheduled(fixedDelay = 3_600_000L)
    public void loadSystemStats() {
        systemStatsLoader.update();
    }

    @Async
    @Scheduled(fixedDelay = 60_000L)
    public void loadPlayerStats() {
        playerStatsLoader.update();
    }
}
