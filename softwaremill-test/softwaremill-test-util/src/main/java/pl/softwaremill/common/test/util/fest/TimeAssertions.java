package pl.softwaremill.common.test.util.fest;

import org.fest.assertions.GenericAssert;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

import static java.lang.String.format;

public class TimeAssertions {

    public static final String PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";

    public static DateAssert assertTime(DateTime date) {
        return new DateAssert(DateAssert.class, date);
    }

    public static LocalDateTimeAssert assertTime(LocalDateTime localDateTime) {
        return new LocalDateTimeAssert(LocalDateTimeAssert.class, localDateTime);
    }

    public static class DateAssert extends GenericAssert<DateAssert, DateTime> {

        /**
         * Creates a new <code>{@link org.fest.assertions.GenericAssert}</code>.
         *
         * @param selfType the "self type"
         * @param actual   the actual value to verify.
         */
        protected DateAssert(Class<DateAssert> selfType, DateTime actual) {
            super(selfType, actual);
        }

        public DateAssert isBefore(DateTime moment) {
            if (!actual.isBefore(moment)) {
                fail(format("Moment %s is not before %s", actual.toString(PATTERN), moment.toString(PATTERN)));
            }

            return this;
        }

        public DateAssert isBeforeOrAt(DateTime moment) {
            if (actual.isAfter(moment)) {
                fail(format("Moment %s is not before %s nor at the same time.", actual.toString(PATTERN), moment.toString(PATTERN)));
            }

            return this;
        }

        public DateAssert isAfterOrAt(DateTime moment) {
            if (actual.isBefore(moment)) {
                fail(format("Moment %s is not after %s nor at the same time", actual.toString(PATTERN), moment.toString(PATTERN)));
            }

            return this;
        }

        public DateAssert isAfter(DateTime moment) {
            if (!actual.isAfter(moment)) {
                fail(format("Moment %s is not after %s", actual.toString(PATTERN), moment.toString(PATTERN)));
            }

            return this;
        }
    }

    public static class LocalDateTimeAssert extends GenericAssert<LocalDateTimeAssert, LocalDateTime> {

        private final DateAssert delegate;

        protected LocalDateTimeAssert(Class<LocalDateTimeAssert> selfType, LocalDateTime actual) {
            super(selfType, actual);
            delegate = new DateAssert(DateAssert.class, actual.toDateTime(DateTimeZone.UTC));
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
