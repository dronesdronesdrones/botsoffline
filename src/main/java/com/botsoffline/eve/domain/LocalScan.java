package com.botsoffline.eve.domain;

import java.time.Instant;

public class LocalScan {
    private String user;
    private String scan;
    private Instant instant;

    public LocalScan() {
    }

    public LocalScan(final String user, final String scan) {
        this.user = user;
        this.scan = scan;
        instant = Instant.now();
    }

    public String getUser() {
        return user;
    }

    public void setUser(final String user) {
        this.user = user;
    }

    public String getScan() {
        return scan;
    }

    public void setScan(final String scan) {
        this.scan = scan;
    }

    public Instant getInstant() {
        return instant;
    }

    public void setInstant(final Instant instant) {
        this.instant = instant;
    }
}
