package pl.softwaremill.common.test.util.fest;

import org.joda.time.DateTime;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static pl.softwaremill.common.test.util.fest.TimeAssertions.assertTime;

public class TimeAssertionsTest {

    //Test isBefore

    @Test(dataProvider = "isBeforeProvider")
    public void isBeforeShouldAssertThatTimeIsBefore(DateTime dateToCheck, DateTime reference) {
        assertTime(dateToCheck).isBefore(reference);
    }

    @Test(dataProvider = "isAfterProvider", expectedExceptions = AssertionError.class)
    public void isBeforeShouldFailForTimeThatIsNotBefore(DateTime dateToCheck, DateTime reference) {
        assertTime(dateToCheck).isBefore(reference);
    }

    @Test(dataProvider = "isEqualProvider", expectedExceptions = AssertionError.class)
    public void isBeforeShouldFailForTimeThatIsEqual(DateTime dateToCheck, DateTime reference) {
        assertTime(dateToCheck).isBefore(reference);
    }


    //Test isBeforeOrAt

    @Test(dataProvider = "isBeforeProvider")
    public void isBeforeOrAtShouldAssertThatTimeIsBefore(DateTime dateToCheck, DateTime reference) {
        assertTime(dateToCheck).isBeforeOrAt(reference);
    }

    @Test(dataProvider = "isEqualProvider")
    public void isBeforeOrAtShouldAssertThatTimeIsEqual(DateTime dateToCheck, DateTime reference) {
        assertTime(dateToCheck).isBeforeOrAt(reference);
    }

    @Test(dataProvider = "isAfterProvider", expectedExceptions = AssertionError.class)
    public void isBeforeOrAtShouldFailWhenTimeIsAfter(DateTime dateToCheck, DateTime reference) {
        assertTime(dateToCheck).isBeforeOrAt(reference);
    }


    //Test isAfter

    @Test(dataProvider = "isBeforeProvider", expectedExceptions = AssertionError.class)
    public void isAfterShouldFailWhenTimeIsBefore(DateTime dateToCheck, DateTime reference) {
        assertTime(dateToCheck).isAfter(reference);
    }

    @Test(dataProvider = "isEqualProvider", expectedExceptions = AssertionError.class)
    public void isAfterShouldFailWhenTimeIsEqual(DateTime dateToCheck, DateTime reference) {
        assertTime(dateToCheck).isAfter(reference);
    }

    @Test(dataProvider = "isAfterProvider")
    public void isAfterShouldAssertThatTimeIsAfter(DateTime dateToCheck, DateTime reference) {
        assertTime(dateToCheck).isAfter(reference);
    }


    //Test isAfterOrAt

    @Test(dataProvider = "isBeforeProvider", expectedExceptions = AssertionError.class)
    public void isAfterOrAtShouldFailWhenTimeIsBefore(DateTime dateToCheck, DateTime reference) {
        assertTime(dateToCheck).isAfterOrAt(reference);
    }

    @Test(dataProvider = "isEqualProvider")
    public void isAfterOrAtShouldFailWhenTimeIsEqual(DateTime dateToCheck, DateTime reference) {
        assertTime(dateToCheck).isAfterOrAt(reference);
    }

    @Test(dataProvider = "isAfterProvider")
    public void isAfterOrAtShouldAssertThatTimeIsAfter(DateTime dateToCheck, DateTime reference) {
        assertTime(dateToCheck).isAfterOrAt(reference);
    }


    @DataProvider
    public Object[][] isBeforeProvider() {
        return new Object[][] {
                new Object[] { new DateTime(2000, 12, 13, 22, 15, 15, 876), new DateTime(2000, 12, 14, 0, 0, 0) },
                new Object[] { new DateTime(1900, 1, 1, 23, 59, 59, 999), new DateTime(1900, 1, 2, 0, 0, 0) },
        };
    }

    @DataProvider
    public Object[][] isAfterProvider() {
        return new Object[][] {
                new Object[] { new DateTime(2000, 12, 14, 22, 15, 15, 876), new DateTime(2000, 12, 14, 22, 15, 15, 875) },
                new Object[] { new DateTime(1900, 1, 2, 0, 0, 0, 1), new DateTime(1900, 1, 2, 0, 0, 0) },
        };
    }

    @DataProvider
    public Object[][] isEqualProvider() {
        return new Object[][] {
                new Object[] { new DateTime(2000, 12, 14, 22, 15, 15, 876), new DateTime(2000, 12, 14, 22, 15, 15, 876) },
                new Object[] { new DateTime(1900, 1, 2, 0, 0, 0, 0), new DateTime(1900, 1, 2, 0, 0, 0, 0) },
        };
    }

}
