/*
 * SolarSystemStats.java
 *
 * Created on 2017-12-10
 *
 */

package com.botsoffline.eve.domain;

import java.time.Instant;

import org.springframework.data.annotation.Id;

public class SolarSystemStats {
    @Id
    private String id;
    private long systemId;
    private int shipKills;
    private int npcKills;
    private int podKills;
    private int jumps;
    private Instant instant;

    public SolarSystemStats() {
    }

    public SolarSystemStats(final long systemId, final int shipKills, final int npcKills, final int podKills, final int jumps) {
        this.systemId = systemId;
        this.shipKills = shipKills;
        this.npcKills = npcKills;
        this.podKills = podKills;
        this.jumps = jumps;
        instant = Instant.now();
    }

    public int getJumps() {
        return jumps;
    }

    public void setJumps(final int jumps) {
        this.jumps = jumps;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public long getSystemId() {
        return systemId;
    }

    public void setSystemId(final long systemId) {
        this.systemId = systemId;
    }

    public int getShipKills() {
        return shipKills;
    }

    public void setShipKills(final int shipKills) {
        this.shipKills = shipKills;
    }

    public int getNpcKills() {
        return npcKills;
    }

    public void setNpcKills(final int npcKills) {
        this.npcKills = npcKills;
    }

    public int getPodKills() {
        return podKills;
    }

    public void setPodKills(final int podKills) {
        this.podKills = podKills;
    }

    public Instant getInstant() {
        return instant;
    }

    public void setInstant(final Instant instant) {
        this.instant = instant;
    }
}
