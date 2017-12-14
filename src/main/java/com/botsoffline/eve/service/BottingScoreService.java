package com.botsoffline.eve.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.botsoffline.eve.domain.BottingScore;
import com.botsoffline.eve.domain.BottingScoreEntry;
import com.botsoffline.eve.domain.CharacterSystemStatus;
import com.botsoffline.eve.domain.SolarSystem;
import com.botsoffline.eve.domain.SolarSystemStats;
import com.botsoffline.eve.repository.BottingScoreRepository;
import com.botsoffline.eve.repository.CharacterSystemStatusRepository;
import com.botsoffline.eve.repository.SolarSystemRepository;
import com.botsoffline.eve.repository.SolarSystemStatsRepository;
import com.botsoffline.eve.web.dto.BottingScoreDTO;

import org.springframework.stereotype.Service;

@Service
public class BottingScoreService {

    private final SolarSystemStatsRepository statsRepository;
    private final SolarSystemRepository solarSystemRepository;
    private final BottingScoreRepository scoreRepository;
    private final CharacterSystemStatusRepository characterSystemStatusRepository;

    public BottingScoreService(final SolarSystemStatsRepository statsRepository,
            final SolarSystemRepository solarSystemRepository,
            final BottingScoreRepository scoreRepository,
            final CharacterSystemStatusRepository characterSystemStatusRepository) {
        this.statsRepository = statsRepository;
        this.solarSystemRepository = solarSystemRepository;
        this.scoreRepository = scoreRepository;
        this.characterSystemStatusRepository = characterSystemStatusRepository;
    }

    public void update() {
        final Map<Long, String> systemNames = new HashMap<>();
        final Map<Long, String> systemRegionNames = new HashMap<>();
        final List<SolarSystem> systems = solarSystemRepository.findAll();
        systems.forEach(system -> systemNames.put(system.getSystemId(), system.getName()));
        systems.forEach(system -> systemRegionNames.put(system.getSystemId(), system.getRegionName()));
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
            final long systemid = entry.getKey();
            bottingScores.add(new BottingScoreEntry(systemid, systemNames.get(systemid), systemRegionNames.get(systemid), getScore(entry)));
        }
        bottingScores.sort((p1, p2) -> p2.getScore() - p1.getScore());
        scoreRepository.save(new BottingScore(bottingScores));
    }

    private int getScore(final Entry<Long, List<Integer>> entry) {
        final long totalKills = entry.getValue().stream().mapToLong(i -> i).sum();
        final long averageKills = totalKills / entry.getValue().size();
        final long totalDelta = entry.getValue().stream().mapToLong(i -> Math.abs(i - averageKills)).sum();
        return (int) (totalKills / (totalDelta / 100));
    }

    public List<BottingScoreDTO> getLatest(final int start, final int end) {
        List<CharacterSystemStatus> activeCharacters = characterSystemStatusRepository.findAllByEndIsNull();
        return scoreRepository.findTop1ByOrderByDateDesc().getList().subList(0, 100).stream()
                .map(e -> toBottingScoreDTO(e, activeCharacters)).collect(Collectors.toList());
    }

    private BottingScoreDTO toBottingScoreDTO(final BottingScoreEntry entry, final Collection<CharacterSystemStatus> activeCharacters) {
        final int count = (int) activeCharacters.stream().filter(a -> a.getSystemId() == entry.getSystemId()).count();
        return new BottingScoreDTO(entry.getSystemName(), entry.getRegionName(), entry.getScore(), count);
    }
}
