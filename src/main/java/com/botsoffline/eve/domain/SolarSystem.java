/*
 * SolarSystem.java
 *
 * Created on 2017-12-10
 *
 */

package com.botsoffline.eve.domain;

import org.springframework.data.annotation.Id;

public class SolarSystem {

    @Id
    private Long systemId;
    private String name;
    private double securityStatus;
    private long constellationId;
    private long regionId;
    private String regionName;
    private Long sovHoldingAlliance;

    public SolarSystem() {
    }

    public SolarSystem(final Long systemId, final String name, final double securityStatus,
            final long constellationId) {
        this.systemId = systemId;
        this.name = name;
        this.securityStatus = securityStatus;
        this.constellationId = constellationId;
    }



    public Long getSovHoldingAlliance() {
        return sovHoldingAlliance;
    }

    public void setSovHoldingAlliance(final Long sovHoldingAlliance) {
        this.sovHoldingAlliance = sovHoldingAlliance;
    }

    public Long getSystemId() {
        return systemId;
    }

    public String getName() {
        return name;
    }

    public double getSecurityStatus() {
        return securityStatus;
    }

    public long getConstellationId() {
        return constellationId;
    }

    public long getRegionId() {
        return regionId;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionId(final long regionId) {
        this.regionId = regionId;
    }

    public void setRegionName(final String regionName) {
        this.regionName = regionName;
    }
}
