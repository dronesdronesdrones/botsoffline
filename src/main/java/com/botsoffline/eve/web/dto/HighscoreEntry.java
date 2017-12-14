package com.botsoffline.eve.web.dto;

public class HighscoreEntry {
    private String name;
    private long score;

    public HighscoreEntry() {
    }

    public HighscoreEntry(final String name, final long score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public long getScore() {
        return score;
    }

    public void setScore(final long score) {
        this.score = score;
    }
}
