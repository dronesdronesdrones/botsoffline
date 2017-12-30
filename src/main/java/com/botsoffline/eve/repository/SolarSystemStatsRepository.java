package com.botsoffline.eve.repository;

import java.time.Instant;
import java.util.List;

import com.botsoffline.eve.domain.SolarSystemStats;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface SolarSystemStatsRepository extends MongoRepository<SolarSystemStats, String> {
    List<SolarSystemStats> findBySystemIdAndInstantAfterOrderByInstantAsc(long systemId, Instant instant);
    List<SolarSystemStats> findAllByInstantAfter(Instant instant);
    List<SolarSystemStats> findBySystemIdOrderByInstantAsc(long systemId);
}
