package com.botsoffline.eve.web.rest;

import com.botsoffline.eve.domain.LocalScan;
import com.botsoffline.eve.repository.LocalScanRepository;
import com.botsoffline.eve.security.SecurityUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/local-scan")
public class LocalScanResource {

    private final Logger log = LoggerFactory.getLogger(LocalScanResource.class);

    private final LocalScanRepository scanRepo;

    public LocalScanResource(final LocalScanRepository scanRepo) {
        this.scanRepo = scanRepo;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void postSolarScan(@RequestBody final String payload) {
        scanRepo.save(new LocalScan(SecurityUtils.getCurrentUserLogin(), payload));
    }
}
