/*
 * TokenRepository.java
 *
 * Created on 2017-12-10
 *
 */

package com.botsoffline.eve.repository;

import java.util.List;

import com.botsoffline.eve.domain.Token;
import com.botsoffline.eve.domain.Token;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


/**
 * Spring Data MongoDB repository for the Doctrine entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TokenRepository extends MongoRepository<Token,String> {
    List<Token> findByClientId(String clientId);
}
