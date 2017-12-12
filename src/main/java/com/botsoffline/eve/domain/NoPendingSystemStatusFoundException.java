package com.botsoffline.eve.domain;

public class NoPendingSystemStatusFoundException extends Exception {
    private static final long serialVersionUID = 7294112599967490435L;

    private final long characterId;

    public NoPendingSystemStatusFoundException(final long characterId) {
        this.characterId = characterId;
    }

    public long getCharacterId() {
        return characterId;
    }

    @Override
    public String toString() {
        return "NoPendingSystemStatusFoundException{" +
               "characterId=" + characterId +
               '}';
    }
}
