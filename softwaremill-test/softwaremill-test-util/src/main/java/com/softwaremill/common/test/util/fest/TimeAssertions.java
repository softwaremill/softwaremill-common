package com.softwaremill.common.test.util.fest;

import org.assertj.core.api.AbstractAssert;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

import java.util.Date;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.fail;

public class TimeAssertions {

    public static final String PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";

    public static DateTimeAssert assertTime(DateTime date) {
        return new DateTimeAssert(DateTimeAssert.class, date);
    }

    public static DateTimeAssert assertTime(Date date) {
        return new DateTimeAssert(DateTimeAssert.class, new DateTime(date.getTime()));
    }

    public static LocalDateTimeAssert assertTime(LocalDateTime localDateTime) {
        return new LocalDateTimeAssert(LocalDateTimeAssert.class, localDateTime);
    }

    public static class DateTimeAssert extends AbstractAssert<DateTimeAssert, DateTime> {

        /**
         * @param selfType the "self type"
         * @param actual   the actual value to verify
         */
        protected DateTimeAssert(Class<DateTimeAssert> selfType, DateTime actual) {
            super(actual, selfType);
        }

        public DateTimeAssert isBefore(DateTime moment) {
            if (!actual.isBefore(moment)) {
                fail(format("Moment %s is not before %s", actual.toString(PATTERN), moment.toString(PATTERN)));
            }

            return this;
        }

        public DateTimeAssert isBefore(Date moment) {
            if (!actual.isBefore(new DateTime(moment))) {
                fail(format("Moment %s is not before %s", actual.toString(PATTERN), new DateTime(moment).toString(PATTERN)));
            }

            return this;
        }

        public DateTimeAssert isBeforeOrAt(DateTime moment) {
            if (actual.isAfter(moment)) {
                fail(format("Moment %s is not before %s nor at the same time.", actual.toString(PATTERN), moment.toString(PATTERN)));
            }

            return this;
        }

        public DateTimeAssert isBeforeOrAt(Date moment) {
            if (actual.isAfter(new DateTime(moment))) {
                fail(format("Moment %s is not before %s nor at the same time.", actual.toString(PATTERN), new DateTime(moment).toString(PATTERN)));
            }

            return this;
        }

        public DateTimeAssert isAfterOrAt(DateTime moment) {
            if (actual.isBefore(moment)) {
                fail(format("Moment %s is not after %s nor at the same time", actual.toString(PATTERN), moment.toString(PATTERN)));
            }

            return this;
        }

        public DateTimeAssert isAfterOrAt(Date moment) {
            if (actual.isBefore(new DateTime(moment))) {
                fail(format("Moment %s is not after %s nor at the same time", actual.toString(PATTERN), new DateTime(moment).toString(PATTERN)));
            }

            return this;
        }

        public DateTimeAssert isAfter(DateTime moment) {
            if (!actual.isAfter(moment)) {
                fail(format("Moment %s is not after %s", actual.toString(PATTERN), moment.toString(PATTERN)));
            }

            return this;
        }

        public DateTimeAssert isAfter(Date moment) {
            if (!actual.isAfter(new DateTime(moment))) {
                fail(format("Moment %s is not after %s", actual.toString(PATTERN), new DateTime(moment).toString(PATTERN)));
            }

            return this;
        }
    }

    public static class LocalDateTimeAssert extends AbstractAssert<LocalDateTimeAssert, LocalDateTime> {

        private final DateTimeAssert delegate;

        protected LocalDateTimeAssert(Class<LocalDateTimeAssert> selfType, LocalDateTime actual) {
            super(actual, selfType);
            delegate = new DateTimeAssert(DateTimeAssert.class, actual.toDateTime(DateTimeZone.UTC));
        }

        public LocalDateTimeAssert isBefore(LocalDateTime moment) {
            delegate.isBefore(moment.toDateTime(DateTimeZone.UTC));
            return this;
        }

        public LocalDateTimeAssert isBeforeOrAt(LocalDateTime moment) {
            delegate.isBeforeOrAt(moment.toDateTime(DateTimeZone.UTC));
            return this;
        }

        public LocalDateTimeAssert isAfterOrAt(LocalDateTime moment) {
            delegate.isAfterOrAt(moment.toDateTime(DateTimeZone.UTC));
            return this;
        }

        public LocalDateTimeAssert isAfter(LocalDateTime moment) {
            delegate.isAfter(moment.toDateTime(DateTimeZone.UTC));
            return this;
        }
    }

}
