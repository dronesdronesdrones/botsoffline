package com.botsoffline.eve.service;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.github.jhipster.config.JHipsterConstants;

@Component
@Profile(JHipsterConstants.SPRING_PROFILE_PRODUCTION)
public class TriggerMachine {

    private final SystemStatsLoader systemStatsLoader;
    private final CharacterLocationLoader playerStatsLoader;
    private final UserService userService;
    private final BottingScoreService bottingScoreService;

    public TriggerMachine(final SystemStatsLoader systemStatsLoader,
            final CharacterLocationLoader playerStatsLoader, final UserService userService,
            final BottingScoreService bottingScoreService) {
        this.systemStatsLoader = systemStatsLoader;
        this.playerStatsLoader = playerStatsLoader;
        this.userService = userService;
        this.bottingScoreService = bottingScoreService;
    }

    @PostConstruct
    public void init() {
        loadSystemStats();
    }

    @Async
    @Scheduled(cron = "0 30 * * * *")
    public void loadSystemStats() {
        systemStatsLoader.update();
    }

    @Async
    @Scheduled(cron = "0 45 * * * *")
    public void updateSov() {
        systemStatsLoader.updateSov();
    }

    @Async
    @Scheduled(fixedDelay = 60_000L)
    public void loadPlayerStats() {
        playerStatsLoader.update();
    }

    @Async
    @Scheduled(fixedDelay = 60000)
    public void initAffiliations() {
        userService.initAffiliations();
    }

    @Async
    @Scheduled(cron = "0 0 0 * * *")
    public void updateAffiliations() {
        userService.updateAffiliations();
    }

    @Async
    @Scheduled(cron = "0 0 11 * * *")
    public void updateBottingScores() {
        bottingScoreService.update();
    }

}
