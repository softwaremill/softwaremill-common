package pl.softwaremill.common.test.util.fest;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pl.softwaremill.common.test.util.AssertException;

import java.util.Date;

import static pl.softwaremill.common.test.util.fest.TimeAssertions.assertTime;

public class TimeAssertionsTest {

    //Test isBefore

    @Test(dataProvider = "referenceBeforeAfterProvider")
    public void isBeforeShouldAssertThatTimeIsBefore(final DateTime reference, DateTime before, final DateTime after) {
        assertTime(before).isBefore(reference);
        assertTime(before.toDate()).isBefore(reference);
        assertTime(before).isBefore(reference.toDate());
        assertTime(before.toDate()).isBefore(reference.toDate());
        assertTime(before.toLocalDateTime()).isBefore(reference.toLocalDateTime());

        assertIsBeforeThrowsException(reference, reference);
        assertIsBeforeThrowsException(reference, reference.toDate());
        assertIsBeforeThrowsException(reference.toDate(), reference);
        assertIsBeforeThrowsException(reference.toDate(), reference.toDate());
        assertIsBeforeThrowsException(reference.toLocalDateTime(), reference.toLocalDateTime());

        assertIsBeforeThrowsException(after, reference);
        assertIsBeforeThrowsException(after.toDate(), reference);
        assertIsBeforeThrowsException(after, reference.toDate());
        assertIsBeforeThrowsException(after.toDate(), reference.toDate());
        assertIsBeforeThrowsException(after.toLocalDateTime(), reference.toLocalDateTime());
    }


    //Test isBeforeOrAt

    @Test(dataProvider = "referenceBeforeAfterProvider")
    public void isBeforeOrAtShouldAssertThatTimeIsBeforeOrEqual(final DateTime reference, DateTime before, final DateTime after) {
        assertTime(before).isBeforeOrAt(reference);
        assertTime(before.toDate()).isBeforeOrAt(reference);
        assertTime(before).isBeforeOrAt(reference.toDate());
        assertTime(before.toDate()).isBeforeOrAt(reference.toDate());
        assertTime(before.toLocalDateTime()).isBeforeOrAt(reference.toLocalDateTime());

        assertTime(reference).isBeforeOrAt(reference);
        assertTime(reference.toDate()).isBeforeOrAt(reference);
        assertTime(reference).isBeforeOrAt(reference.toDate());
        assertTime(reference.toDate()).isBeforeOrAt(reference.toDate());
        assertTime(reference.toLocalDateTime()).isBeforeOrAt(reference.toLocalDateTime());

        assertIsBeforeOrAtThrowsException(after, reference);
        assertIsBeforeOrAtThrowsException(after.toDate(), reference);
        assertIsBeforeOrAtThrowsException(after, reference.toDate());
        assertIsBeforeOrAtThrowsException(after.toDate(), reference.toDate());
        assertIsBeforeOrAtThrowsException(after.toLocalDateTime(), reference.toLocalDateTime());
    }


    //Test isAfter

    @Test(dataProvider = "referenceBeforeAfterProvider")
    public void isAfterShouldAssertThatTimeIsAfter(final DateTime reference, final DateTime before, final DateTime after) {
        assertIsAfterThrowsException(before, reference);
        assertIsAfterThrowsException(before.toDate(), reference);
        assertIsAfterThrowsException(before, reference.toDate());
        assertIsAfterThrowsException(before.toDate(), reference.toDate());
        assertIsAfterThrowsException(before.toLocalDateTime(), reference.toLocalDateTime());

        assertIsAfterThrowsException(reference, reference);
        assertIsAfterThrowsException(reference.toDate(), reference);
        assertIsAfterThrowsException(reference, reference.toDate());
        assertIsAfterThrowsException(reference.toDate(), reference.toDate());
        assertIsAfterThrowsException(reference.toLocalDateTime(), reference.toLocalDateTime());

        assertTime(after).isAfter(reference);
        assertTime(after.toDate()).isAfter(reference);
        assertTime(after).isAfter(reference.toDate());
        assertTime(after.toDate()).isAfter(reference.toDate());
        assertTime(after.toLocalDateTime()).isAfter(reference.toLocalDateTime());
    }


    //Test isAfterOrAt
    @Test(dataProvider = "referenceBeforeAfterProvider")
    public void isAfterOrAtShouldAssertThatTimeIsAfterOrEqual(final DateTime reference, final DateTime before, final DateTime after) {
        assertIsAfterOrAtThrowsException(before, reference);
        assertIsAfterOrAtThrowsException(before.toDate(), reference);
        assertIsAfterOrAtThrowsException(before, reference.toDate());
        assertIsAfterOrAtThrowsException(before.toDate(), reference.toDate());
        assertIsAfterOrAtThrowsException(before.toLocalDateTime(), reference.toLocalDateTime());

        assertIsAfterOrAtThrowsException(reference, reference);
        assertIsAfterOrAtThrowsException(reference.toDate(), reference);
        assertIsAfterOrAtThrowsException(reference, reference.toDate());
        assertIsAfterOrAtThrowsException(reference.toDate(), reference.toDate());
        assertIsAfterOrAtThrowsException(reference.toLocalDateTime(), reference.toLocalDateTime());

        assertTime(after).isAfterOrAt(reference);
        assertTime(after.toDate()).isAfterOrAt(reference);
        assertTime(after).isAfterOrAt(reference.toDate());
        assertTime(after.toDate()).isAfterOrAt(reference.toDate());
        assertTime(after.toLocalDateTime()).isAfterOrAt(reference.toLocalDateTime());
    }

    @DataProvider
    public Object[][] referenceBeforeAfterProvider() {
        return new Object[][] {
                new Object[] { new DateTime(2000, 12, 14, 0, 0), new DateTime(2000, 12, 13, 23, 59, 59, 999), new DateTime(2000, 12, 14, 0, 0, 0, 1) },
                new Object[] { new DateTime(2000, 12, 14, 22, 15, 15, 875), new DateTime(2000, 12, 14, 22, 15, 15, 874), new DateTime(2000, 12, 14, 22, 15, 15, 876) },
        };
    }


    private void assertIsBeforeThrowsException(final Date dateToCheck, final DateTime reference) {
        AssertException.thrown(AssertionError.class, new Runnable() {
            @Override
            public void run() {
                assertTime(dateToCheck).isBefore(reference);
            }
        });
    }

    private void assertIsBeforeThrowsException(final Date dateToCheck, final Date reference) {
        AssertException.thrown(AssertionError.class, new Runnable() {
            @Override
            public void run() {
                assertTime(dateToCheck).isBefore(reference);
            }
        });
    }

    private void assertIsBeforeThrowsException(final DateTime dateToCheck, final Date reference) {
        AssertException.thrown(AssertionError.class, new Runnable() {
            @Override
            public void run() {
                assertTime(dateToCheck).isBefore(reference);
            }
        });
    }

    private void assertIsBeforeThrowsException(final LocalDateTime dateToCheck, final LocalDateTime reference) {
        AssertException.thrown(AssertionError.class, new Runnable() {
            @Override
            public void run() {
                assertTime(dateToCheck).isBefore(reference);
            }
        });
    }

    private void assertIsBeforeThrowsException(final DateTime dateToCheck, final DateTime reference) {
        AssertException.thrown(AssertionError.class, new Runnable() {
            @Override
            public void run() {
                assertTime(dateToCheck).isBefore(reference);
            }
        });
    }


    private void assertIsBeforeOrAtThrowsException(final Date dateToCheck, final Date reference) {
        AssertException.thrown(AssertionError.class, new Runnable() {
            @Override
            public void run() {
                assertTime(dateToCheck).isBeforeOrAt(reference);
            }
        });
    }

    private void assertIsBeforeOrAtThrowsException(final Date dateToCheck, final DateTime reference) {
        AssertException.thrown(AssertionError.class, new Runnable() {
            @Override
            public void run() {
                assertTime(dateToCheck).isBeforeOrAt(reference);
            }
        });
    }

    private void assertIsBeforeOrAtThrowsException(final DateTime dateToCheck, final Date reference) {
        AssertException.thrown(AssertionError.class, new Runnable() {
            @Override
            public void run() {
                assertTime(dateToCheck).isBeforeOrAt(reference);
            }
        });
    }

    private void assertIsBeforeOrAtThrowsException(final LocalDateTime dateToCheck, final LocalDateTime reference) {
        AssertException.thrown(AssertionError.class, new Runnable() {
            @Override
            public void run() {
                assertTime(dateToCheck).isBeforeOrAt(reference);
            }
        });
    }

    private void assertIsBeforeOrAtThrowsException(final DateTime dateToCheck, final DateTime reference) {
        AssertException.thrown(AssertionError.class, new Runnable() {
            @Override
            public void run() {
                assertTime(dateToCheck).isBeforeOrAt(reference);
            }
        });
    }


    private void assertIsAfterThrowsException(final Date dateToCheck, final DateTime reference) {
        AssertException.thrown(AssertionError.class, new Runnable() {
            @Override
            public void run() {
                assertTime(dateToCheck).isAfter(reference);
            }
        });
    }

    private void assertIsAfterThrowsException(final Date dateToCheck, final Date reference) {
        AssertException.thrown(AssertionError.class, new Runnable() {
            @Override
            public void run() {
                assertTime(dateToCheck).isAfter(reference);
            }
        });
    }

    private void assertIsAfterThrowsException(final DateTime dateToCheck, final Date reference) {
        AssertException.thrown(AssertionError.class, new Runnable() {
            @Override
            public void run() {
                assertTime(dateToCheck).isAfter(reference);
            }
        });
    }

    private void assertIsAfterThrowsException(final LocalDateTime dateToCheck, final LocalDateTime reference) {
        AssertException.thrown(AssertionError.class, new Runnable() {
            @Override
            public void run() {
                assertTime(dateToCheck).isAfter(reference);
            }
        });
    }

    private void assertIsAfterThrowsException(final DateTime dateToCheck, final DateTime reference) {
        AssertException.thrown(AssertionError.class, new Runnable() {
            @Override
            public void run() {
                assertTime(dateToCheck).isAfter(reference);
            }
        });
    }


    private void assertIsAfterOrAtThrowsException(final Date dateToCheck, final DateTime reference) {
        AssertException.thrown(AssertionError.class, new Runnable() {
            @Override
            public void run() {
                assertTime(dateToCheck).isAfterOrAt(reference);
            }
        });
    }

    private void assertIsAfterOrAtThrowsException(final DateTime dateToCheck, final Date reference) {
        AssertException.thrown(AssertionError.class, new Runnable() {
            @Override
            public void run() {
                assertTime(dateToCheck).isAfterOrAt(reference);
            }
        });
    }

    private void assertIsAfterOrAtThrowsException(final Date dateToCheck, final Date reference) {
        AssertException.thrown(AssertionError.class, new Runnable() {
            @Override
            public void run() {
                assertTime(dateToCheck).isAfterOrAt(reference);
            }
        });
    }

    private void assertIsAfterOrAtThrowsException(final LocalDateTime dateToCheck, final LocalDateTime reference) {
        AssertException.thrown(AssertionError.class, new Runnable() {
            @Override
            public void run() {
                assertTime(dateToCheck).isAfterOrAt(reference);
            }
        });
    }

    private void assertIsAfterOrAtThrowsException(final DateTime dateToCheck, final DateTime reference) {
        AssertException.thrown(AssertionError.class, new Runnable() {
            @Override
            public void run() {
                assertTime(dateToCheck).isAfterOrAt(reference);
            }
        });
    }

}
