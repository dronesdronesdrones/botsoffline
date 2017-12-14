package com.botsoffline.eve.web.dto;

public class BottingScoreDTO {
    private String systemName;
    private String regionName;
    private int score;
    private int activeCount;

    public BottingScoreDTO(final String systemName, final String regionName, final int score, final int activeCount) {
        this.systemName = systemName;
        this.regionName = regionName;
        this.score = score;
        this.activeCount = activeCount;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(final String systemName) {
        this.systemName = systemName;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(final String regionName) {
        this.regionName = regionName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(final int score) {
        this.score = score;
    }

    public int getActiveCount() {
        return activeCount;
    }

    public void setActiveCount(final int activeCount) {
        this.activeCount = activeCount;
    }
}
