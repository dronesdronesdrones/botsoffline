package com.botsoffline.eve.repository;

import java.time.Instant;
import java.util.List;

import com.botsoffline.eve.domain.CharacterScore;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CharacterScoreRepository extends MongoRepository<CharacterScore, String> {
    List<CharacterScore> findAllByInstantAfterAndCharacterIdNotIn(Instant instant, List<Long> characterIds);

    List<CharacterScore> findAllByInstantAfterAndCharacterId(Instant instant, Long characterId);

    CharacterScore findTop1ByCharacterIdAndScoreOrderByInstantDesc(long characterId, int score);
}
