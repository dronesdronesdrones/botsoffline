package com.botsoffline.eve.repository;

import java.util.List;

import com.botsoffline.eve.domain.CharacterLocation;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CharacterLocationRepository extends MongoRepository<CharacterLocation, String> {
    CharacterLocation findTop1ByCharacterIdOrderByInstantDesc(long characterId);
    long countByCharacterId(long characterId);

    List<CharacterLocation> findAllByCharacterId(Long characterId);
}
