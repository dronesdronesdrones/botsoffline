package com.botsoffline.eve.repository;

import java.util.List;

import com.botsoffline.eve.domain.SolarSystemStats;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface SolarSystemStatsRepository extends MongoRepository<SolarSystemStats, String> {
    List<SolarSystemStats> findBySystemIdOrderByInstantAsc(long systemId);
}
