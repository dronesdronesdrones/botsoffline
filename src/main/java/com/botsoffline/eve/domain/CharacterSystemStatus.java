package com.botsoffline.eve.domain;

import java.time.Instant;

import org.springframework.data.annotation.Id;

public class CharacterSystemStatus {
    @Id
    private String id;
    private long characterId;
    private long systemId;
    private Instant start;
    private Instant end;

    public CharacterSystemStatus() {
    }

    public CharacterSystemStatus(final String id, final long characterId, final long systemId, final Instant start,
            final Instant end) {
        this.id = id;
        this.characterId = characterId;
        this.systemId = systemId;
        this.start = start;
        this.end = end;
    }

    public CharacterSystemStatus(final long characterId, final long systemId, final Instant start) {
        this.characterId = characterId;
        this.systemId = systemId;
        this.start = start;
    }

    public long getCharacterId() {
        return characterId;
    }

    public void setCharacterId(final long characterId) {
        this.characterId = characterId;
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

    public Instant getStart() {
        return start;
    }

    public void setStart(final Instant start) {
        this.start = start;
    }

    public Instant getEnd() {
        return end;
    }

    public void setEnd(final Instant end) {
        this.end = end;
    }
}
