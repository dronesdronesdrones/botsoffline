package com.botsoffline.eve.service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.botsoffline.eve.domain.SolarSystem;
import com.botsoffline.eve.domain.SolarSystemStats;
import com.botsoffline.eve.repository.SolarSystemRepository;
import com.botsoffline.eve.repository.SolarSystemStatsRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SystemStatsLoader {

    private final Logger log = LoggerFactory.getLogger(SystemStatsLoader.class);

    private final JsonRequestService requestService;
    private final SolarSystemRepository solarSystemRepository;
    private final SolarSystemStatsRepository statsRepository;

    public SystemStatsLoader(final JsonRequestService requestService,
            final SolarSystemRepository solarSystemRepository,
            final SolarSystemStatsRepository statsRepository) {
        this.requestService = requestService;
        this.solarSystemRepository = solarSystemRepository;
        this.statsRepository = statsRepository;
    }

    public void initSolarSystems() {
        if (solarSystemRepository.count() == 0) {
            log.info("Initializing solar systems.");
            final List<SolarSystem> systems = requestService.getAllSystemIds().parallelStream()
                    .map(requestService::isNullsec)
                    .filter(system -> system.getSecurityStatus() <= 0)
                    .filter(system -> !system.getName().startsWith("J"))
                    .collect(Collectors.toList());
            solarSystemRepository.save(systems);
            update();
        } else {
            log.warn("Skipped solar system initialization as there are more than 0 systems present.");
        }
    }

    void update() {
        log.debug("Updating solarSystemStats.");
        final List<Long> systemIds = solarSystemRepository.findAll().parallelStream().map(SolarSystem::getSystemId)
                .collect(Collectors.toList());
        final List<SolarSystemStats> result = requestService.getSolarSystemStats().parallelStream()
                .filter(e -> isValidSystem(e, systemIds))
                .collect(Collectors.toList());
        statsRepository.save(result);
        log.info("Updated solarSystemStats.");
    }

    private boolean isValidSystem(final SolarSystemStats system, final Collection<Long> systemIds) {
        return systemIds.contains(system.getSystemId());
    }
}
