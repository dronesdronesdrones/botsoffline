/*
 * UserRepository.java
 *
 * Created on 2017-12-10
 *
 */

package com.botsoffline.eve.repository;

import java.util.List;
import java.util.Optional;

import com.botsoffline.eve.domain.User;
import com.botsoffline.eve.domain.enums.TrackingStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the User entity.
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findOneByLogin(String login);

    Page<User> findAllByLoginNot(Pageable pageable, String login);

    Optional<User> findOneByCharacterId(Long characterId);

    List<User> findAllByTrackingStatus(TrackingStatus trackingStatus);

    List<User> findAllByCorporationIdIsNull();
}
