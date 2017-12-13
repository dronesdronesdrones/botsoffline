package com.botsoffline.eve.repository;

import com.botsoffline.eve.domain.BottingScore;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface BottingScoreRepository extends MongoRepository<BottingScore, String> {
    BottingScore findTop1ByOrderByDateDesc();
}
