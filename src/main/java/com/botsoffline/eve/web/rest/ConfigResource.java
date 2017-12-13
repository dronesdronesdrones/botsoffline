/*
 * ConfigResource.java
 *
 * Created on 2017-12-10
 *
 */

package com.botsoffline.eve.web.rest;

import com.botsoffline.eve.service.BottingScoreService;
import com.botsoffline.eve.service.SystemStatsLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/config")
public class ConfigResource {

    private final Logger log = LoggerFactory.getLogger(ConfigResource.class);

    @Value("${SSO_URL}")
    private String SSO_URL;

    @Value("${ADMIN_SECRET}")
    private String ADMIN_SECRET;

    private SystemStatsLoader systemStatsLoader;
    private BottingScoreService bottingScoreService;

    public ConfigResource(final SystemStatsLoader systemStatsLoader,
            final BottingScoreService bottingScoreService) {
        this.systemStatsLoader = systemStatsLoader;
        this.bottingScoreService = bottingScoreService;
    }

    @GetMapping(path = "/ssourl")
    public ResponseEntity<String> getSsoUrl() {
        return new ResponseEntity<>(SSO_URL, HttpStatus.OK);
    }

    @PutMapping(path = "/{auth}/init-solar-systems")
    public void initSolarSystems(@PathVariable final String auth) {
        if (ADMIN_SECRET.equals(auth)) {
            systemStatsLoader.initSolarSystems();
        } else {
            log.warn("Unauthorized init-solar-systems call.");
        }
    }

    @PutMapping(path = "/{auth}/update-scores")
    public void updateScores(@PathVariable final String auth) {
        if (ADMIN_SECRET.equals(auth)) {
            bottingScoreService.update();
        } else {
            log.warn("Unauthorized update-scores call.");
        }
    }

}
