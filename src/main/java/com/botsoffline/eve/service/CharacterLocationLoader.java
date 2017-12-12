package com.botsoffline.eve.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.botsoffline.eve.domain.Activity;
import com.botsoffline.eve.domain.CharacterLocation;
import com.botsoffline.eve.domain.SolarSystem;
import com.botsoffline.eve.domain.User;
import com.botsoffline.eve.domain.enums.TrackingStatus;
import com.botsoffline.eve.repository.ActivityRepository;
import com.botsoffline.eve.repository.CharacterLocationRepository;
import com.botsoffline.eve.repository.SolarSystemRepository;
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
    private final ActivityRepository activityRepository;
    private final SolarSystemRepository solarSystemRepository;
    private final CharacterSystemStatusService characterSystemStatusService;
    private long minutesForUpdate;

    public CharacterLocationLoader(final CharacterLocationRepository locationRepository,
            final JsonRequestService requestService, final UserRepository userRepository,
            final ActivityRepository activityRepository,
            final SolarSystemRepository solarSystemRepository,
            final CharacterSystemStatusService characterSystemStatusService) {
        this.locationRepository = locationRepository;
        this.requestService = requestService;
        this.userRepository = userRepository;
        this.activityRepository = activityRepository;
        this.solarSystemRepository = solarSystemRepository;
        this.characterSystemStatusService = characterSystemStatusService;
    }

    @Timed
    public void update() {
        log.debug("Updating playerStats.");
        final Instant before = Instant.now();
        final List<Long> solarSystemIds = solarSystemRepository.findAll().stream()
                .map(SolarSystem::getSystemId)
                .collect(Collectors.toList());
        final List<Activity> activities = activityRepository.findAllByDate(LocalDate.now());
        final List<CharacterLocation> result = userRepository.findAllByTrackingStatus(TrackingStatus.ENABLED)
                .stream().map(this::getLocationStats)
                .filter(Objects::nonNull)
                .filter(stat -> solarSystemIds.contains(stat.getSystemId()))
                .peek(location -> extendActivities(location, activities))
                .collect(Collectors.toList());
        locationRepository.save(result);
        activityRepository.save(activities);
        final Instant after = Instant.now();
        minutesForUpdate = before.until(after, ChronoUnit.MINUTES);
        log.info("Updated {} playerStats.", result.size());

        // todo: score modifier
    }

    private void extendActivities(final CharacterLocation location, final Collection<Activity> activities) {
        final Optional<Activity> optional = activities.stream()
                .filter(a -> a.getCharacterId() == location.getCharacterId()
                       && a.getSystemId() == location.getSystemId())
                .findFirst();
        if (optional.isPresent()) {
            optional.get().addOneMinute();
        } else {
            activities.add(new Activity(location.getCharacterId(), location.getSystemId(), 1, LocalDate.now()));
        }
    }

    private CharacterLocation getLocationStats(final User user) {
        final String accessToken = getAccessToken(user);
        if (null != accessToken) {
            return getLocationIfOnline(user.getCharacterId(), accessToken);
        } else {
            characterSystemStatusService.complete(user.getCharacterId());
            return null;
        }
    }

    private CharacterLocation getLocationIfOnline(final long characterId, final String accessToken) {
        if (!requestService.isOnline(characterId, accessToken)) {
            characterSystemStatusService.complete(characterId);
            return null;
        }
        final CharacterLocation location = requestService.getLocation(characterId, accessToken);
        characterSystemStatusService.createOrUpdate(characterId, location.getSystemId());
        return location;
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
            log.warn("Could not get an access token for {} {}. Deleting the user so he has to create a new refresh "
                     + "token.", user.getLogin(), user.getCharacterId());
            userRepository.delete(user.getId());
            return null;
        }
    }

    public long getLoginDelay() {
        return minutesForUpdate <= 2 ? 5 : minutesForUpdate * 2;
    }
}
