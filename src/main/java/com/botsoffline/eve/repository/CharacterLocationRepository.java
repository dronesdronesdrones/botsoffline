package com.botsoffline.eve.repository;

import com.botsoffline.eve.domain.CharacterLocation;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CharacterLocationRepository extends MongoRepository<CharacterLocation, String> {
}
