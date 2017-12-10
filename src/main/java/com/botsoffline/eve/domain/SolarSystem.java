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

    public SolarSystem() {
    }

    public SolarSystem(final Long systemId, final String name, final double securityStatus,
            final long constellationId) {
        this.systemId = systemId;
        this.name = name;
        this.securityStatus = securityStatus;
        this.constellationId = constellationId;
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
}
