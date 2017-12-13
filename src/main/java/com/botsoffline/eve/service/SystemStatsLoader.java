package com.botsoffline.eve.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.botsoffline.eve.domain.SolarSystem;
import com.botsoffline.eve.domain.SolarSystemStats;
import com.botsoffline.eve.domain.SovInfo;
import com.botsoffline.eve.repository.SolarSystemRepository;
import com.botsoffline.eve.repository.SolarSystemStatsRepository;
import com.mashape.unirest.http.JsonNode;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
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

    @Async
    public void initSolarSystems() {
        if (solarSystemRepository.count() == 0) {
            log.info("Initializing solar systems.");
            final Map<Long, JSONObject> constellations = new HashMap<>();
            final List<SolarSystem> systems = requestService.getAllSystemIds().parallelStream()
                    .map(requestService::isNullsec)
                    .filter(system -> system.getSecurityStatus() <= 0)
                    .filter(system -> system.getName().length() < 7)
                    .peek(system -> updateRegion(system, constellations))
                    .collect(Collectors.toList());
            solarSystemRepository.save(systems);
            log.info("Added nullsec systems. Continuing with stats.");
            update();
        } else {
            log.warn("Skipped solar system initialization as there are more than 0 systems present.");
        }
    }

    private void updateRegion(final SolarSystem system, final Map<Long, JSONObject> constellations) {
        final long constellationId = system.getConstellationId();
        if (!constellations.containsKey(constellationId)) {
            downloadNewConstellation(constellationId, constellations);
        }
        final JSONObject data = constellations.get(constellationId);
        system.setRegionId(data.getLong("region_id"));
        system.setRegionName(data.getString("region_name"));
    }

    private void downloadNewConstellation(final long constellationId, final Map<Long, JSONObject> constellations) {
        final Optional<JsonNode> constellation = requestService.getConstellation(constellationId);
        if (constellation.isPresent()) {
            final JSONObject obj = constellation.get().getObject();
            final long regionId = obj.getLong("region_id");
            final String regionName = requestService.getRegionName(regionId);
            if (null != regionName) {
                obj.put("region_name", regionName);
                constellations.put(constellationId, obj);
            } else {
                log.error("Could not get data for region {}.", regionId);
            }
        } else {
            log.error("Could not get data for constellation {}.", constellationId);
        }
    }

    void update() {
        log.debug("Updating solarSystemStats.");
        final List<Long> systemIds = solarSystemRepository.findAll().parallelStream().map(SolarSystem::getSystemId)
                .collect(Collectors.toList());
        final List<SolarSystemStats> result = requestService.getSolarSystemStats().stream()
                .filter(e -> isValidSystem(e, systemIds))
                .collect(Collectors.toList());
        statsRepository.save(result);
        log.info("Updated solarSystemStats.");
    }

    void updateSov() {
        final List<SovInfo> sovInfos = requestService.getSovInformation();
        final List<SolarSystem> systems = solarSystemRepository.findAll().stream()
                .map(e -> {
                    boolean updated = setSovInfoIfAvailable(e, sovInfos);
                    return updated ? e : null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        solarSystemRepository.save(systems);
    }

    /**
     * Returns true if the sov was updated.
     *
     * @param system
     * @param sovInfos
     * @return
     */
    private boolean setSovInfoIfAvailable(final SolarSystem system, final Iterable<SovInfo> sovInfos) {
        for (final SovInfo sovInfo : sovInfos) {
            if (system.getSystemId() == sovInfo.getSystemId()) {
                if (system.getSovHoldingAlliance() != null && system.getSovHoldingAlliance() != sovInfo.getAllianceId()) {
                    system.setSovHoldingAlliance(sovInfo.getAllianceId());
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    private boolean isValidSystem(final SolarSystemStats system, final Collection<Long> systemIds) {
        return systemIds.contains(system.getSystemId());
    }

}
