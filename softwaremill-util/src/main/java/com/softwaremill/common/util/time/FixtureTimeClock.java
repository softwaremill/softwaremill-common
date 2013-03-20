package com.softwaremill.common.util.time;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * Class responsible for providing arbitrary fixed current time value.
 */
public class FixtureTimeClock implements Clock {

    private long millis;

    public FixtureTimeClock(long millis) {
        this.millis = millis;
    }

    @Override
    public DateTime currentDateTime() {
        return new DateTime(millis);
    }

    @Override
    public DateTime currentDateTimeUTC() {
        return new DateTime(millis, DateTimeZone.UTC);
    }

    @Override
    public long currentTimeMillis() {
        return millis;
    }
}