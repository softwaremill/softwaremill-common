package com.softwaremill.common.backup;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public enum SimpleDBRegion {
    EUROPE_WEST("sdb.eu-west-1.amazonaws.com"),
    US_EAST("sdb.amazonaws.com"),
    US_WEST("sdb.us-west-1.amazonaws.com"),
    ASIA_SOUTHEAST("sdb.ap-southeast-1.amazonaws.com");

    private final String address;

    SimpleDBRegion(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }
}
