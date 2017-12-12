package com.botsoffline.eve.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import com.botsoffline.eve.domain.CharacterSystemStatus;
import com.botsoffline.eve.domain.NoPendingSystemStatusFoundException;
import com.botsoffline.eve.repository.CharacterSystemStatusRepository;

import org.springframework.stereotype.Service;

/**
 * Service used to keep track of who entered which system at which time to determine who was first, second, ...
 */
@Service
public class CharacterSystemStatusService {

    private final CharacterSystemStatusRepository repository;

    public CharacterSystemStatusService(final CharacterSystemStatusRepository repository) {
        this.repository = repository;
    }

    void complete(final Long characterId) {
        repository.findByCharacterIdAndEndIsNull(characterId).ifPresent(stat -> {
            stat.setEnd(Instant.now());
            repository.save(stat);
        });
    }

    private void start(final long characterId, final long systemId) {
        repository.save(new CharacterSystemStatus(characterId, systemId, Instant.now()));
    }

    int activeInMinutes(final long characterId, final long systemId) {
        return repository.findByCharacterIdAndSystemIdAndEndIsNull(characterId, systemId)
                .map(characterSystemStatus ->
                             (int) characterSystemStatus.getStart().until(Instant.now(), ChronoUnit.MINUTES))
                .orElse(0);
    }

    void createOrUpdate(final long characterId, final long systemId) {
        final Optional<CharacterSystemStatus> optional = repository.findByCharacterIdAndEndIsNull(characterId);
        if (optional.isPresent()) {
            final CharacterSystemStatus stat = optional.get();
            if (stat.getSystemId() != systemId) {
                stat.setEnd(Instant.now());
                repository.save(stat);
                start(characterId, systemId);
            }
            // DONT start a new one if the char is still in the same system
        } else {
            start(characterId, systemId);
        }
    }

    public int getRankInSystem(final long characterId) throws NoPendingSystemStatusFoundException {
        final int[] rank = {0};
        repository.findByCharacterIdAndEndIsNull(characterId).ifPresent(stat -> {
            rank[0] = 1;
            for (final CharacterSystemStatus s : repository.findAllBySystemIdAndEndIsNullOrderByStartAsc(stat.getSystemId())) {
                if (characterId == s.getCharacterId()) {
                    break;
                } else {
                    rank[0]++;
                }
            }
        });
        // rank[0] == 0 means that findByCharacterIdAndEndIsNull did not yield any result
        if (rank[0] == 0) {
            throw new NoPendingSystemStatusFoundException(characterId);
        } else {
            return rank[0];
        }
    }
}
