/*
 * AuthorityRepository.java
 *
 * Created on 2017-12-10
 *
 */

package com.botsoffline.eve.repository;

import com.botsoffline.eve.domain.Authority;
import com.botsoffline.eve.domain.Authority;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the Authority entity.
 */
public interface AuthorityRepository extends MongoRepository<Authority, String> {
}
