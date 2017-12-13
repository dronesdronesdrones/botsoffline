package com.botsoffline.eve.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;

public class BottingScore {
    @Id
    private LocalDate date;
    private List<BottingScoreEntry> list = new ArrayList<>();

    public BottingScore() {
    }

    public BottingScore(final List<BottingScoreEntry> list) {
        date = LocalDate.now();
        this.list = list;
    }

    public BottingScore(final LocalDate date, final List<BottingScoreEntry> list) {
        this.date = date;
        this.list = list;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(final LocalDate date) {
        this.date = date;
    }

    public List<BottingScoreEntry> getList() {
        return list;
    }

    public void setList(final List<BottingScoreEntry> list) {
        this.list = list;
    }
}
