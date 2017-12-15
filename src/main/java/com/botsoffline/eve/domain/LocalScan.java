package com.botsoffline.eve.domain;

public class LocalScan {
    private String user;
    private String scan;

    public LocalScan() {
    }

    public LocalScan(final String user, final String scan) {
        this.user = user;
        this.scan = scan;
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
}
