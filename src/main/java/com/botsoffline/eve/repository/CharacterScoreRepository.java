package com.botsoffline.eve.repository;

import com.botsoffline.eve.domain.CharacterScore;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CharacterScoreRepository extends MongoRepository<CharacterScore, String> {
}
