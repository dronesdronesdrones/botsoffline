package com.botsoffline.eve.web.rest;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import com.botsoffline.eve.domain.CharacterScore;
import com.botsoffline.eve.domain.LocalScan;
import com.botsoffline.eve.domain.User;
import com.botsoffline.eve.repository.CharacterScoreRepository;
import com.botsoffline.eve.repository.LocalScanRepository;
import com.botsoffline.eve.repository.UserRepository;
import com.botsoffline.eve.security.SecurityUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/local-scan")
public class LocalScanResource {

    private final Logger log = LoggerFactory.getLogger(LocalScanResource.class);

    private final LocalScanRepository scanRepo;
    private final CharacterScoreRepository characterScoreRepository;
    private final UserRepository userRepository;

    public LocalScanResource(final LocalScanRepository scanRepo,
            final CharacterScoreRepository characterScoreRepository,
            final UserRepository userRepository) {
        this.scanRepo = scanRepo;
        this.characterScoreRepository = characterScoreRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity postSolarScan(@RequestBody final String payload) {
        final String login = SecurityUtils.getCurrentUserLogin();
        final User user = userRepository.findByLogin(login);
        final CharacterScore score = characterScoreRepository
                .findTop1ByCharacterIdAndScoreOrderByInstantDesc(user.getCharacterId(), 100);
        scanRepo.save(new LocalScan(login, payload));
        if (null == score || score.getInstant().until(Instant.now(), ChronoUnit.HOURS) >= 1) {
            addPoints(user.getCharacterId());
            return ResponseEntity.status(201).build();
        } else {
            return ResponseEntity.ok().build();
        }
    }

    @Async
    private void addPoints(final long characterId) {
        characterScoreRepository.save(new CharacterScore(characterId, -1L, 100));
        log.info("Awarded local scan points to {}.", characterId);
    }
}
