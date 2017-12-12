package com.botsoffline.eve.domain;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;

public class Activity {
    @Id
    private String id;
    private long characterId;
    private long systemId;
    private int minutes;
    private LocalDate date;

    public Activity() {
    }

    public Activity(final long characterId, final long systemId, final int minutes, final LocalDate date) {
        this.characterId = characterId;
        this.systemId = systemId;
        this.minutes = minutes;
        this.date = date;
    }

    public Activity(final String id, final long characterId, final long systemId, final int minutes, final LocalDate date) {
        this.id = id;
        this.characterId = characterId;
        this.systemId = systemId;
        this.minutes = minutes;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public long getCharacterId() {
        return characterId;
    }

    public void setCharacterId(final long characterId) {
        this.characterId = characterId;
    }

    public long getSystemId() {
        return systemId;
    }

    public void setSystemId(final long systemId) {
        this.systemId = systemId;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(final int minutes) {
        this.minutes = minutes;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(final LocalDate date) {
        this.date = date;
    }

    public void addOneMinute() {
        minutes++;
    }
}
