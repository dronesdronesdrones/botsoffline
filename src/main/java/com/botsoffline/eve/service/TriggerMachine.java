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
    private final UserService userService;

    public TriggerMachine(final SystemStatsLoader systemStatsLoader,
            final CharacterLocationLoader playerStatsLoader, final UserService userService) {
        this.systemStatsLoader = systemStatsLoader;
        this.playerStatsLoader = playerStatsLoader;
        this.userService = userService;
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
    @Scheduled(cron = "0 0 * * * *")
    public void updateSov() {
        systemStatsLoader.update();
    }

    @Async
    @Scheduled(fixedDelay = 60_000L)
    public void loadPlayerStats() {
        playerStatsLoader.update();
    }

    @Async
    @Scheduled(fixedDelay = 30000)
    public void initAffiliations() {
        userService.initAffiliations();
    }

    @Async
    @Scheduled(cron = "0 0 0 * * *")
    public void updateAffiliations() {
        userService.updateAffiliations();
    }

}
