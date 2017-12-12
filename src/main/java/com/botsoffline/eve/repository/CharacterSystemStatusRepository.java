package com.botsoffline.eve.repository;

import java.util.List;
import java.util.Optional;

import com.botsoffline.eve.domain.CharacterSystemStatus;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CharacterSystemStatusRepository extends MongoRepository<CharacterSystemStatus, String> {
    Optional<CharacterSystemStatus> findByCharacterIdAndEndIsNull(long characterId);

    Optional<CharacterSystemStatus> findByCharacterIdAndSystemIdAndEndIsNull(long characterId, long systemId);

    List<CharacterSystemStatus> findAllBySystemIdAndEndIsNullOrderByStartAsc(long systemId);
}
