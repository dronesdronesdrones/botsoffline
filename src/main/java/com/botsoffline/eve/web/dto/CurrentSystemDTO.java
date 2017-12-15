package com.botsoffline.eve.web.dto;

public class CurrentSystemDTO {
    private final String name;
    private final boolean inOwnSov;
    private final int score;

    public CurrentSystemDTO(final String name, final boolean inOwnSov, final int score) {
        this.name = name;
        this.inOwnSov = inOwnSov;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public boolean isInOwnSov() {
        return inOwnSov;
    }

    public int getScore() {
        return score;
    }
}
