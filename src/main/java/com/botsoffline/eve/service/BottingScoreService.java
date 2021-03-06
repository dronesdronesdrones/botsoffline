package com.botsoffline.eve.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
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
import com.codahale.metrics.annotation.Timed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class BottingScoreService {

    private final Logger log = LoggerFactory.getLogger(BottingScoreService.class);

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

    @Timed
    public void update() {
        final Map<Long, String> systemNames = new HashMap<>();
        final Map<Long, String> systemRegionNames = new HashMap<>();
        final List<SolarSystem> systems = solarSystemRepository.findAll();
        systems.forEach(system -> systemNames.put(system.getSystemId(), system.getName()));
        systems.forEach(system -> systemRegionNames.put(system.getSystemId(), system.getRegionName()));
        final List<SolarSystemStats> allStats = statsRepository.findAll();
        final Map<Long, List<SolarSystemStats>> dataPerSystem = new HashMap<>();
        for (final SolarSystemStats stats : allStats) {
            final long systemId = stats.getSystemId();
            if (!dataPerSystem.containsKey(systemId)) {
                dataPerSystem.put(systemId, new ArrayList<>());
            }
            dataPerSystem.get(systemId).add(stats);
        }
        final List<BottingScoreEntry> bottingScores = new ArrayList<>();
        for (final Entry<Long, List<SolarSystemStats>> entry : dataPerSystem.entrySet()) {
            final long systemid = entry.getKey();
            bottingScores.add(new BottingScoreEntry(systemid, systemNames.get(systemid), systemRegionNames.get(systemid), getScore(entry.getValue())));
        }
        bottingScores.sort((p1, p2) -> p2.getScore() - p1.getScore());
        scoreRepository.save(new BottingScore(bottingScores));
    }

    /** stats must be sorted by instant asc
     * @param stats**/
    private int getScore(final List<SolarSystemStats> stats) {
        final long pvpKills = stats.stream().mapToLong(SolarSystemStats::getShipKills).sum();
        final long npcKills = stats.stream().mapToLong(SolarSystemStats::getNpcKills).sum();
        final long averageKills = (int) (npcKills / stats.size());
        final long npcDelta = stats.stream().mapToLong(SolarSystemStats::getNpcKills)
                .map(l -> Math.abs(l - averageKills)).sum();

        final int pvpModifier = (int) (pvpKills * 5);
        int score = (int) (npcKills / (npcDelta / 100.0));
        score = pvpModifier >= score ? 0 : score - pvpModifier;
        log.debug("Score {}, PvP Kills {}", score, pvpKills);
        return score;
    }

    public List<BottingScoreDTO> getLatest(final int start, final int end) {
        final List<CharacterSystemStatus> activeCharacters = characterSystemStatusRepository.findAllByEndIsNull();
        return scoreRepository.findTop1ByOrderByDateDesc().getList().subList(start, end).stream()
                .map(e -> toBottingScoreDTO(e, activeCharacters)).collect(Collectors.toList());
    }

    private BottingScoreDTO toBottingScoreDTO(final BottingScoreEntry entry,
            final Collection<CharacterSystemStatus> activeCharacters) {
        final int count = (int) activeCharacters.stream().filter(a -> a.getSystemId() == entry.getSystemId()).count();
        return new BottingScoreDTO(entry.getSystemName(), entry.getRegionName(), entry.getScore(), count);
    }

    List<BottingScoreEntry> getLatest() {
        return scoreRepository.findTop1ByOrderByDateDesc().getList();
    }

    @Async
    public void removeDuplicates() {
        final List<Long> systemIds = solarSystemRepository.findAll().stream().map(SolarSystem::getSystemId)
                .collect(Collectors.toList());
        for (final long systemId : systemIds) {
            int cleanCount = 0;
            final List<SolarSystemStats> stats = statsRepository.findBySystemIdOrderByInstantAsc(systemId);
            int latestHour = 0;
            for (final SolarSystemStats stat : stats) {
                final int hour = LocalDateTime.ofInstant(stat.getInstant(), ZoneId.systemDefault()).getHour();
                if (latestHour == hour) {
                    cleanCount++;
                    statsRepository.delete(stat);
                } else {
                    latestHour = hour;
                }
            }
            log.info("Cleaned up {} entries for system {}.", cleanCount, systemId);
        }
        log.info("CleanUpSystemScores complete.");
    }

    public int getScoreOfSystem(final long systemId) {
        final List<SolarSystemStats> npcKills = statsRepository.findBySystemIdAndInstantAfterOrderByInstantAsc(systemId, Instant.now().minus(10, ChronoUnit.DAYS)).stream()
                .collect(Collectors.toList());
        return getScore(npcKills);
    }
}
