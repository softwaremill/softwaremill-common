package com.softwaremill.common.util.time;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * Provides time basing on system clock.
 */
public class RealTimeClock implements Clock {

    @Override
    public DateTime currentDateTime() {
        return DateTime.now();
    }

    @Override
    public DateTime currentDateTimeUTC() {
        return DateTime.now(DateTimeZone.UTC);
    }

    @Override
    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }
}