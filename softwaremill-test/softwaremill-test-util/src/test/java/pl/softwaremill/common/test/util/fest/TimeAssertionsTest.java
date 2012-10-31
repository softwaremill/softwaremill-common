package pl.softwaremill.common.test.util.fest;

import org.joda.time.DateTime;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pl.softwaremill.common.test.util.AssertException;

import static pl.softwaremill.common.test.util.fest.TimeAssertions.assertTime;

public class TimeAssertionsTest {

    //Test isBefore

    @Test(dataProvider = "referenceBeforeAfterProvider")
    public void isBeforeShouldAssertThatTimeIsBefore(final DateTime reference, DateTime before, final DateTime after) {
        assertTime(before).isBefore(reference);
        assertTime(before.toLocalDateTime()).isBefore(reference.toLocalDateTime());

        AssertException.thrown(AssertionError.class, new Runnable() {
            @Override
            public void run() {
                assertTime(reference).isBefore(reference);
            }
        });
        AssertException.thrown(AssertionError.class, new Runnable() {
            @Override
            public void run() {
                assertTime(reference.toLocalDateTime()).isBefore(reference.toLocalDateTime());
            }
        });

        AssertException.thrown(AssertionError.class, new Runnable() {
            @Override
            public void run() {
                assertTime(after).isBefore(reference);
            }
        });
        AssertException.thrown(AssertionError.class, new Runnable() {
            @Override
            public void run() {
                assertTime(after.toLocalDateTime()).isBefore(reference.toLocalDateTime());
            }
        });
    }


    //Test isBeforeOrAt

    @Test(dataProvider = "referenceBeforeAfterProvider")
    public void isBeforeOrAtShouldAssertThatTimeIsBeforeOrEqual(final DateTime reference, DateTime before, final DateTime after) {
        assertTime(before).isBeforeOrAt(reference);
        assertTime(before.toLocalDateTime()).isBeforeOrAt(reference.toLocalDateTime());

        assertTime(reference).isBeforeOrAt(reference);
        assertTime(reference.toLocalDateTime()).isBeforeOrAt(reference.toLocalDateTime());

        AssertException.thrown(AssertionError.class, new Runnable() {
            @Override
            public void run() {
                assertTime(after).isBeforeOrAt(reference);
            }
        });
        AssertException.thrown(AssertionError.class, new Runnable() {
            @Override
            public void run() {
                assertTime(after.toLocalDateTime()).isBeforeOrAt(reference.toLocalDateTime());
            }
        });
    }


    //Test isAfter

    @Test(dataProvider = "referenceBeforeAfterProvider")
    public void isAfterShouldAssertThatTimeIsAfter(final DateTime reference, final DateTime before, final DateTime after) {
        AssertException.thrown(AssertionError.class, new Runnable() {
            @Override
            public void run() {
                assertTime(before).isAfter(reference);
            }
        });
        AssertException.thrown(AssertionError.class, new Runnable() {
            @Override
            public void run() {
                assertTime(before.toLocalDateTime()).isAfter(reference.toLocalDateTime());
            }
        });

        AssertException.thrown(AssertionError.class, new Runnable() {
            @Override
            public void run() {
                assertTime(reference).isAfter(reference);
            }
        });
        AssertException.thrown(AssertionError.class, new Runnable() {
            @Override
            public void run() {
                assertTime(reference.toLocalDateTime()).isAfter(reference.toLocalDateTime());
            }
        });

        assertTime(after).isAfter(reference);
        assertTime(after.toLocalDateTime()).isAfter(reference.toLocalDateTime());
    }


    //Test isAfterOrAt
    @Test(dataProvider = "referenceBeforeAfterProvider")
    public void isAfterOrAtShouldAssertThatTimeIsAfterOrEqual(final DateTime reference, final DateTime before, final DateTime after) {
        AssertException.thrown(AssertionError.class, new Runnable() {
            @Override
            public void run() {
                assertTime(before).isAfterOrAt(reference);
            }
        });
        AssertException.thrown(AssertionError.class, new Runnable() {
            @Override
            public void run() {
                assertTime(before.toLocalDateTime()).isAfterOrAt(reference.toLocalDateTime());
            }
        });

        assertTime(reference).isAfterOrAt(reference);
        assertTime(reference.toLocalDateTime()).isAfterOrAt(reference.toLocalDateTime());

        assertTime(after).isAfterOrAt(reference);
        assertTime(after.toLocalDateTime()).isAfterOrAt(reference.toLocalDateTime());
    }

    @DataProvider
    public Object[][] referenceBeforeAfterProvider() {
        return new Object[][] {
                new Object[] { new DateTime(2000, 12, 14, 0, 0), new DateTime(2000, 12, 13, 23, 59, 59, 999), new DateTime(2000, 12, 14, 0, 0, 0, 1) },
                new Object[] { new DateTime(2000, 12, 14, 22, 15, 15, 875), new DateTime(2000, 12, 14, 22, 15, 15, 874), new DateTime(2000, 12, 14, 22, 15, 15, 876) },
        };
    }

}
