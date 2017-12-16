package com.botsoffline.eve.repository;

import com.botsoffline.eve.domain.LocalScan;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface LocalScanRepository extends MongoRepository<LocalScan, String> {
    LocalScan findTop1ByUserOrderByInstantDesc(String user);
}
