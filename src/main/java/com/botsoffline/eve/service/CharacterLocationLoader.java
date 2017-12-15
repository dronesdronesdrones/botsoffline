package com.botsoffline.eve.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.botsoffline.eve.domain.Activity;
import com.botsoffline.eve.domain.BottingScoreEntry;
import com.botsoffline.eve.domain.CharacterLocation;
import com.botsoffline.eve.domain.CharacterScore;
import com.botsoffline.eve.domain.NoPendingSystemStatusFoundException;
import com.botsoffline.eve.domain.SolarSystem;
import com.botsoffline.eve.domain.User;
import com.botsoffline.eve.domain.enums.TrackingStatus;
import com.botsoffline.eve.repository.ActivityRepository;
import com.botsoffline.eve.repository.CharacterLocationRepository;
import com.botsoffline.eve.repository.CharacterScoreRepository;
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
    private final BottingScoreService bottingScoreService;
    private final CharacterScoreRepository characterScoreRepository;
    private long minutesForUpdate;

    public CharacterLocationLoader(final CharacterLocationRepository locationRepository,
            final JsonRequestService requestService, final UserRepository userRepository,
            final ActivityRepository activityRepository,
            final SolarSystemRepository solarSystemRepository,
            final CharacterSystemStatusService characterSystemStatusService,
            final BottingScoreService bottingScoreService,
            final CharacterScoreRepository characterScoreRepository) {
        this.locationRepository = locationRepository;
        this.requestService = requestService;
        this.userRepository = userRepository;
        this.activityRepository = activityRepository;
        this.solarSystemRepository = solarSystemRepository;
        this.characterSystemStatusService = characterSystemStatusService;
        this.bottingScoreService = bottingScoreService;
        this.characterScoreRepository = characterScoreRepository;
    }

    @Timed
    public void update() {
        log.debug("Updating playerStats.");
        final Instant before = Instant.now();
        final List<SolarSystem> solarSystems = solarSystemRepository.findAll();
        final List<Activity> activities = activityRepository.findAllByDate(LocalDate.now());
        final List<User> allUsers = userRepository.findAllByTrackingStatus(TrackingStatus.ENABLED);
        final Map<Long, Long> userSov = new HashMap<>();
        allUsers.forEach(user -> userSov.put(user.getCharacterId(), user.getAllianceId()));
        final List<CharacterLocation> result = allUsers
                .stream().map(this::getLocationStats)
                .filter(Objects::nonNull)
                .filter(location -> isInSystemList(location.getSystemId(), solarSystems))
                .peek(location -> markIfInOwnSov(location, userSov, solarSystems))
                .peek(location -> extendActivities(location, activities))
                .collect(Collectors.toList());
        locationRepository.save(result);
        activityRepository.save(activities);
        final Instant after = Instant.now();
        minutesForUpdate = before.until(after, ChronoUnit.MINUTES);

        // calculate scores
        final List<BottingScoreEntry> latestScores = bottingScoreService.getLatest();
        final List<CharacterScore> scores = result.stream()
                .filter(location -> !location.isInOwnSov())
                .map(characterLocation -> toScoreEntry(characterLocation, latestScores))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        characterScoreRepository.save(scores);

        log.info("Updated {} playerStats.", result.size());
    }

    private void markIfInOwnSov(final CharacterLocation location, final Map<Long, Long> userSov,
            final List<SolarSystem> solarSystems) {
        final Long sovHolder = getSovHolder(location, solarSystems);
        final Long characterAlliance = userSov.get(location.getCharacterId());
        if (null == sovHolder || null == characterAlliance) {
            location.setInOwnSov(false);
        } else {
            location.setInOwnSov(sovHolder.equals(characterAlliance));
        }
    }

    private CharacterScore toScoreEntry(final CharacterLocation characterLocation, final Collection<BottingScoreEntry> systemStatuses) {
        return systemStatuses.stream()
                .filter(stat -> stat.getSystemId() == characterLocation.getSystemId())
                .findFirst()
                .map(bottingScoreEntry -> new CharacterScore(characterLocation.getCharacterId(),
                                                             characterLocation.getSystemId(),
                                                             getScoreForPlayer(characterLocation.getCharacterId(),
                                                                               bottingScoreEntry.getScore())))
                .orElse(null);
    }

    private int getScoreForPlayer(final long characterId, final int score) {
        try {
            final int rank = characterSystemStatusService.getRankInSystem(characterId);
            // subtract 30% from the score for each rank behind the first rank, bottom out at 0
            // divide by 100, to remove the inflated value of the system score
            final double modifier = 1.0 - (0.3 * (rank - 1));
            return (int) ((score / 100.0) * (modifier > 0 ? modifier : 0));
        } catch (final NoPendingSystemStatusFoundException e) {
            log.info("No pending system could be found for character {} while adding scores.", characterId);
            return 0;
        }
    }

    private Long getSovHolder(final CharacterLocation location, final List<SolarSystem> solarSystems) {
        Long sovHolder = null;
        for (final SolarSystem solarSystem : solarSystems) {
            if (solarSystem.getSystemId().equals(location.getSystemId())) {
                sovHolder = solarSystem.getSovHoldingAlliance();
            }
        }
        return sovHolder;
    }

    private boolean isInSystemList(final Long systemId, final Iterable<SolarSystem> solarSystems) {
        for (final SolarSystem solarSystem : solarSystems) {
            if (solarSystem.getSystemId().equals(systemId)) {
                return true;
            }
        }
        return false;
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
