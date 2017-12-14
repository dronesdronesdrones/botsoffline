package com.botsoffline.eve.domain;

import java.time.Instant;

public class CharacterScore {
    private long characterId;
    private Long systemId;
    private int score;
    private Instant instant;

    public CharacterScore(final long characterId, final Long systemId, final int score) {
        instant = Instant.now();
        this.characterId = characterId;
        this.systemId = systemId;
        this.score = score;
    }

    public long getCharacterId() {
        return characterId;
    }

    public Long getSystemId() {
        return systemId;
    }

    public int getScore() {
        return score;
    }

    public Instant getInstant() {
        return instant;
    }

    public void setCharacterId(final long characterId) {
        this.characterId = characterId;
    }

    public void setSystemId(final Long systemId) {
        this.systemId = systemId;
    }

    public void setScore(final int score) {
        this.score = score;
    }

    public void setInstant(final Instant instant) {
        this.instant = instant;
    }
}
