package com.botsoffline.eve.web.rest;

import java.util.List;

import com.botsoffline.eve.security.SecurityUtils;
import com.botsoffline.eve.service.CharacterScoreService;
import com.botsoffline.eve.web.dto.HighscoreEntry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api")
public class HighscoreResource {

    private final Logger log = LoggerFactory.getLogger(HighscoreResource.class);

    private final CharacterScoreService service;

    public HighscoreResource(final CharacterScoreService service) {
        this.service = service;
    }

    @GetMapping("/highscore/days/{days}/length/{length}")
    public ResponseEntity<List<HighscoreEntry>> getHighscore(@PathVariable final int days, @PathVariable final int length) {
        if (days != 7 && days != 30) {
            log.info("Retrieving highscore for {} days by {}.", days, SecurityUtils.getCurrentUserLogin());
        }
        return ResponseEntity.ok(service.getHighscore(days, length));
    }

    @GetMapping("/score/days/{days}")
    public ResponseEntity<Long> getScore(@PathVariable final int days) {
        return ResponseEntity.ok(service.getScore(SecurityUtils.getCurrentUserLogin(), days));
    }
}
