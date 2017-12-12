package com.botsoffline.eve.repository;

import java.time.LocalDate;
import java.util.List;

import com.botsoffline.eve.domain.Activity;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the Authority entity.
 */
public interface ActivityRepository extends MongoRepository<Activity, String> {
    List<Activity> findAllByDate(LocalDate date);
}
