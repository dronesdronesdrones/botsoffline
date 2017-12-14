package com.botsoffline.eve.web.rest;

import java.time.Instant;
import java.util.Optional;

import com.botsoffline.eve.domain.CharacterLocation;
import com.botsoffline.eve.domain.NoPendingSystemStatusFoundException;
import com.botsoffline.eve.domain.SolarSystem;
import com.botsoffline.eve.domain.User;
import com.botsoffline.eve.repository.CharacterLocationRepository;
import com.botsoffline.eve.repository.SolarSystemRepository;
import com.botsoffline.eve.repository.UserRepository;
import com.botsoffline.eve.security.SecurityUtils;
import com.botsoffline.eve.service.CharacterLocationLoader;
import com.botsoffline.eve.service.CharacterSystemStatusService;
import com.codahale.metrics.annotation.Timed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class StatsResource {

    private final Logger log = LoggerFactory.getLogger(StatsResource.class);

    private final CharacterLocationRepository locationRepository;
    private final UserRepository userRepository;
    private final CharacterLocationLoader locationLoader;
    private final SolarSystemRepository systemRepository;
    private final CharacterSystemStatusService characterSystemStatusService;

    public StatsResource(final CharacterLocationRepository locationRepository,
            final UserRepository userRepository,
            final CharacterLocationLoader locationLoader,
            final SolarSystemRepository systemRepository,
            final CharacterSystemStatusService characterSystemStatusService) {
        this.locationRepository = locationRepository;
        this.userRepository = userRepository;
        this.locationLoader = locationLoader;
        this.systemRepository = systemRepository;
        this.characterSystemStatusService = characterSystemStatusService;
    }

    @GetMapping("/player/current-status")
    @Timed
    public ResponseEntity isAuthenticated() {
        final Optional<User> oneByLogin = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
        if (oneByLogin.isPresent()) {
            final CharacterLocation lastLocation = locationRepository.findTop1ByCharacterIdOrderByInstantDesc(
                    oneByLogin.get().getCharacterId());
            if (lastLocation != null && Instant.now().minusSeconds(locationLoader.getLoginDelay() * 60).isBefore(lastLocation.getInstant())) {
                final SolarSystem system = systemRepository.findBySystemId(lastLocation.getSystemId());
                final BodyBuilder builder = ResponseEntity.ok();
                if (lastLocation.isInOwnSov()) {
                    builder.header("X-OWN-SOV", "true");
                }
                return builder.body(system);
            } else {
                return ResponseEntity.noContent().build();
            }
        } else {
            log.warn("Could not find the user for {}.", SecurityUtils.getCurrentUserLogin());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/player/total-minutes")
    @Timed
    public ResponseEntity getTotalMinutes() {
        final Optional<User> oneByLogin = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
        if (oneByLogin.isPresent()) {
            final long count = locationRepository.countByCharacterId(oneByLogin.get().getCharacterId());
            return ResponseEntity.ok(count);
        } else {
            log.warn("Could not find the user for {}.", SecurityUtils.getCurrentUserLogin());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/player/rank-in-system")
    @Timed
    public ResponseEntity<Integer> getRankInSystem() {
        final Optional<User> oneByLogin = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
        if (oneByLogin.isPresent()) {
            try {
                final int rank = characterSystemStatusService.getRankInSystem(oneByLogin.get().getCharacterId());
                return ResponseEntity.ok(rank);
            } catch (final NoPendingSystemStatusFoundException e) {
                log.debug("No pending system status was found.");
                return ResponseEntity.noContent().build();
            }
        } else {
            log.warn("Could not find the user for {}.", SecurityUtils.getCurrentUserLogin());
            return ResponseEntity.notFound().build();
        }
    }

}
