package com.botsoffline.eve.domain;

public class SovInfo {
    private final long systemId;
    private final long allianceId;

    public SovInfo(final long systemId, final long allianceId) {
        this.systemId = systemId;
        this.allianceId = allianceId;
    }

    public long getSystemId() {
        return systemId;
    }

    public long getAllianceId() {
        return allianceId;
    }
}
