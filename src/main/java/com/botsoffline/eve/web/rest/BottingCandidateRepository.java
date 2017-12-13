package com.botsoffline.eve.web.rest;

import java.util.List;

import com.botsoffline.eve.domain.BottingScoreEntry;
import com.botsoffline.eve.service.BottingScoreService;
import com.codahale.metrics.annotation.Timed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api")
public class BottingCandidateRepository {

    private final Logger log = LoggerFactory.getLogger(BottingCandidateRepository.class);

    private final BottingScoreService bottingScoreService;

    public BottingCandidateRepository(final BottingScoreService bottingScoreService) {
        this.bottingScoreService = bottingScoreService;
    }

    @GetMapping("/candidates")
    @Timed
    public ResponseEntity get() {
        final List<BottingScoreEntry> list = bottingScoreService.getLatest().getList().subList(0, 100);
        return ResponseEntity.ok(list);
    }
}
