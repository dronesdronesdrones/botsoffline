package com.botsoffline.eve.domain;

public class BottingScoreEntry {
    private long systemId;
    private String systemName;
    private String regionName;
    private int score;

    public BottingScoreEntry() {
    }

    public BottingScoreEntry(final long systemId, final String systemName, final String regionName, final int score) {
        this.systemId = systemId;
        this.systemName = systemName;
        this.regionName = regionName;
        this.score = score;
    }

    public long getSystemId() {
        return systemId;
    }

    public void setSystemId(final long systemId) {
        this.systemId = systemId;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(final String systemName) {
        this.systemName = systemName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(final int score) {
        this.score = score;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(final String regionName) {
        this.regionName = regionName;
    }
}
