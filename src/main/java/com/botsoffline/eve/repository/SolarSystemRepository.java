/*
 * SolarSystemRepository.java
 *
 * Created on 2017-12-10
 *
 */

package com.botsoffline.eve.repository;

import com.botsoffline.eve.domain.SolarSystem;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the Authority entity.
 */
public interface SolarSystemRepository extends MongoRepository<SolarSystem, String> {
    SolarSystem findBySystemId(long systemId);
}
