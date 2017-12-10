package com.botsoffline.eve.web.rest;

import java.time.Instant;
import java.util.Optional;

import com.botsoffline.eve.domain.CharacterLocation;
import com.botsoffline.eve.domain.User;
import com.botsoffline.eve.repository.CharacterLocationRepository;
import com.botsoffline.eve.repository.SolarSystemRepository;
import com.botsoffline.eve.repository.UserRepository;
import com.botsoffline.eve.security.SecurityUtils;
import com.botsoffline.eve.service.CharacterLocationLoader;
import com.codahale.metrics.annotation.Timed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
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

    public StatsResource(final CharacterLocationRepository locationRepository,
            final UserRepository userRepository,
            final CharacterLocationLoader locationLoader,
            final SolarSystemRepository systemRepository) {
        this.locationRepository = locationRepository;
        this.userRepository = userRepository;
        this.locationLoader = locationLoader;
        this.systemRepository = systemRepository;
    }

    @GetMapping("/player/current-status")
    @Timed
    public ResponseEntity isAuthenticated() {
        final Optional<User> oneByLogin = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
        if (oneByLogin.isPresent()) {
            final CharacterLocation lastLocation = locationRepository.findTop1ByCharacterIdOrderByInstantDesc(
                    oneByLogin.get().getCharacterId());
            if (lastLocation != null && Instant.now().minusSeconds(locationLoader.getLoginDelay() * 60).isBefore(lastLocation.getInstant())) {
                return ResponseEntity.ok(systemRepository.findBySystemId(lastLocation.getSystemId()));
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

}
