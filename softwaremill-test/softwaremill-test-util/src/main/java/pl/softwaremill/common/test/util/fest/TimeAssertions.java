package pl.softwaremill.common.test.util.fest;

import org.fest.assertions.GenericAssert;
import org.joda.time.DateTime;

import static java.lang.String.format;

public class TimeAssertions {

    public static final String PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";

    public static DateAssert assertThat(DateTime date) {
        return new DateAssert(DateAssert.class, date);
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
}
