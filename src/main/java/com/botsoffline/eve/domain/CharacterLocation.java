package com.botsoffline.eve.domain;

import java.time.Instant;

public class CharacterLocation {
    private long characterId;
    private Long systemId;
    private Long structureId;
    private Instant instant;

    public CharacterLocation() {
    }

    public CharacterLocation(final long characterId, final Long systemId, final Long structureId) {
        this.characterId = characterId;
        this.systemId = systemId;
        this.structureId = structureId;
        instant = Instant.now();
    }

    public long getCharacterId() {
        return characterId;
    }

    public void setCharacterId(final long characterId) {
        this.characterId = characterId;
    }

    public Long getSystemId() {
        return systemId;
    }

    public void setSystemId(final Long systemId) {
        this.systemId = systemId;
    }

    public Long getStructureId() {
        return structureId;
    }

    public void setStructureId(final Long structureId) {
        this.structureId = structureId;
    }

    public Instant getInstant() {
        return instant;
    }

    public void setInstant(final Instant instant) {
        this.instant = instant;
    }
}
