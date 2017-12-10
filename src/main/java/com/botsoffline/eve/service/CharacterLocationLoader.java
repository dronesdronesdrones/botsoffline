package com.botsoffline.eve.service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.botsoffline.eve.domain.CharacterLocation;
import com.botsoffline.eve.domain.User;
import com.botsoffline.eve.repository.CharacterLocationRepository;
import com.botsoffline.eve.repository.UserRepository;
import com.codahale.metrics.annotation.Timed;
import com.mashape.unirest.http.JsonNode;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CharacterLocationLoader {

    private final Logger log = LoggerFactory.getLogger(CharacterLocationLoader.class);

    private final CharacterLocationRepository locationRepository;
    private final JsonRequestService requestService;
    private final UserRepository userRepository;

    public CharacterLocationLoader(final CharacterLocationRepository locationRepository,
            final JsonRequestService requestService, final UserRepository userRepository) {
        this.locationRepository = locationRepository;
        this.requestService = requestService;
        this.userRepository = userRepository;
    }

    @Timed
    public void update() {
        log.debug("Updating playerStats.");
        final List<CharacterLocation> result = userRepository.findAll().stream()
                .map(this::getLocationStats)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        locationRepository.save(result);
        log.info("Updated {} playerStats.", result.size());
    }

    private CharacterLocation getLocationStats(final User user) {
        final String accessToken = getAccessToken(user);
        return requestService.getLocationIfOnline(user.getCharacterId(), accessToken);
    }

    private String getAccessToken(final User user) {
        if (user.getAccessToken() != null && user.getAccessTokenExpiry() != null
            && Instant.now().isBefore(user.getAccessTokenExpiry())) {
            return user.getAccessToken();
        }
        final Optional<JsonNode> tokenOptional = requestService.getAccessToken(user.getRefreshToken());
        if (tokenOptional.isPresent()) {
            log.debug("Loading character location for {}.", user.getLogin());
            final JSONObject object = tokenOptional.get().getObject();
            final String accessToken = object.getString("access_token");
            user.setAccessToken(accessToken);
            final int secondsRemaining = object.getInt("expires_in");
            // reduce by 5 buffer seconds
            user.setAccessTokenExpiry(Instant.now().plusSeconds(secondsRemaining).minusSeconds(5));
            userRepository.save(user);
            return accessToken;
        } else {
            log.warn("Could not get an access token for {} {}.", user.getLogin(), user.getCharacterId());
            return null;
        }
    }
}
