package pl.softwaremill.common.util.time;

import org.joda.time.DateTime;

/**
 * Interface providing current system time.
 */
public interface Clock {

    DateTime currentDateTime();
    DateTime currentDateTimeUTC();
    long currentTimeMillis();
}