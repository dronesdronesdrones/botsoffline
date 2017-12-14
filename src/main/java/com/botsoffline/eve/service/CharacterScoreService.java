package com.botsoffline.eve.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import com.botsoffline.eve.domain.CharacterScore;
import com.botsoffline.eve.domain.User;
import com.botsoffline.eve.repository.CharacterScoreRepository;
import com.botsoffline.eve.repository.UserRepository;
import com.botsoffline.eve.web.dto.HighscoreEntry;
import com.codahale.metrics.annotation.Timed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CharacterScoreService {

    private final Logger log = LoggerFactory.getLogger(CharacterScoreService.class);

    private final UserRepository userRepository;
    private final CharacterScoreRepository scoreRepository;

    public CharacterScoreService(final UserRepository userRepository,
            final CharacterScoreRepository scoreRepository) {
        this.userRepository = userRepository;
        this.scoreRepository = scoreRepository;
    }

    @Timed
    public List<HighscoreEntry> getHighscore(final int days, final int length) {
        final List<User> nonHideUsers = userRepository.findAllByHideFromLeaderboard(false);
        final List<Long> hideCharacterIds = userRepository.findAllByHideFromLeaderboard(true).stream()
                .map(User::getCharacterId).collect(Collectors.toList());
        final List<CharacterScore> scoresSinceDay = scoreRepository.findAllByInstantAfterAndCharacterIdNotIn(
                LocalDateTime.now().minusDays(days).toInstant(ZoneOffset.UTC), hideCharacterIds);
        final Map<Long, Long> scoreMap = new HashMap<>();
        scoresSinceDay.forEach(score -> {
            final long characterId = score.getCharacterId();
            if (!scoreMap.containsKey(characterId)) {
                scoreMap.put(characterId, 0L);
            }
            scoreMap.put(characterId, scoreMap.get(characterId) + score.getScore());
        });
        return scoreMap.entrySet().stream().map(entry -> toHighScoreEntry(entry, nonHideUsers))
                .filter(Objects::nonNull)
                .sorted(this::compareHighscore)
                .collect(Collectors.toList())
                .subList(0, length);
    }

    private int compareHighscore(final HighscoreEntry e1, final HighscoreEntry e2) {
        return (int) (e2.getScore() - e1.getScore());
    }

    private HighscoreEntry toHighScoreEntry(final Entry<Long, Long> score, final Collection<User> users) {
        final Long characterId = score.getKey();
        final String characterName = users.stream().filter(user -> characterId.equals(user.getCharacterId())).findFirst().map(
                User::getLogin).orElse(null);
        if (null != characterName) {
            return new HighscoreEntry(characterName, score.getValue());
        } else {
            log.warn("Could not retrieve the character name for {}.", characterId);
            return null;
        }
    }
}
