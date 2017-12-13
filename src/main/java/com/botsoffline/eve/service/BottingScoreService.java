package com.botsoffline.eve.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.botsoffline.eve.domain.BottingScore;
import com.botsoffline.eve.domain.BottingScoreEntry;
import com.botsoffline.eve.domain.SolarSystemStats;
import com.botsoffline.eve.repository.BottingScoreRepository;
import com.botsoffline.eve.repository.SolarSystemRepository;
import com.botsoffline.eve.repository.SolarSystemStatsRepository;

import org.springframework.stereotype.Service;

@Service
public class BottingScoreService {

    private final SolarSystemStatsRepository statsRepository;
    private final SolarSystemRepository solarSystemRepository;
    private final BottingScoreRepository scoreRepository;

    public BottingScoreService(final SolarSystemStatsRepository statsRepository,
            final SolarSystemRepository solarSystemRepository,
            final BottingScoreRepository scoreRepository) {
        this.statsRepository = statsRepository;
        this.solarSystemRepository = solarSystemRepository;
        this.scoreRepository = scoreRepository;
    }

    public void update() {
        final Map<Long, String> systemNames = new HashMap<>();
        solarSystemRepository.findAll().forEach(system -> systemNames.put(system.getSystemId(), system.getName()));
        final List<SolarSystemStats> allStats = statsRepository.findAll();
        final Map<Long, List<Integer>> dataPerSystem = new HashMap<>();
        for (final SolarSystemStats stats : allStats) {
            final long systemId = stats.getSystemId();
            if (!dataPerSystem.containsKey(systemId)) {
                dataPerSystem.put(systemId, new ArrayList<>());
            }
            dataPerSystem.get(systemId).add(stats.getNpcKills());
        }
        final List<BottingScoreEntry> bottingScores = new ArrayList<>();
        for (final Entry<Long, List<Integer>> entry : dataPerSystem.entrySet()) {
            final long sum = entry.getValue().stream().mapToLong(i -> i).sum();
            final long average = sum / entry.getValue().size();
            final long variance = entry.getValue().stream().mapToLong(i -> Math.abs(i - average)).sum();
            final int someResult = (int) (sum / (variance / 100));
            final long systemid = entry.getKey();
            bottingScores.add(new BottingScoreEntry(systemid, systemNames.get(systemid), someResult));
        }
        bottingScores.sort((p1, p2) -> p2.getScore() - p1.getScore());
        scoreRepository.save(new BottingScore(bottingScores));
    }

    public BottingScore getLatest() {
        return scoreRepository.findTop1ByOrderByDateDesc();
    }
}
