package com.botsoffline.eve.domain;

public class BottingScoreEntry {
    private long systemId;
    private String systemName;
    private int score;

    public BottingScoreEntry() {
    }

    public BottingScoreEntry(final long systemId, final String systemName, final int score) {
        this.systemId = systemId;
        this.systemName = systemName;
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
}
