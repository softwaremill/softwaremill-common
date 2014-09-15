package com.softwaremill.common.util;

import org.joda.time.DateTime;
import org.testng.annotations.Test;

import java.text.ParseException;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Tomasz Dziurko
 */
public class DateUtilTest {

    @Test
    public void shouldCreateDateFromValidStringAndFormat() throws Exception {

        // given
        String dateString = "2012/04/10";
        String format = "yyyy/MM/dd";

        // when
        Date date = DateUtil.createDateFromString(dateString, format);

        // then
        DateTime jodaDate = new DateTime(date);

        assertThat(jodaDate.getDayOfMonth()).isEqualTo(10);
        assertThat(jodaDate.getMonthOfYear()).isEqualTo(04);
        assertThat(jodaDate.getYear()).isEqualTo(2012);
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Date is null!")
    public void shouldThrowExceptionWhenNullPassed() throws Exception {

        // given
        String format = "yyyy/MM/dd";

        // when
        DateUtil.createDateFromString(null, format);
    }

    @Test(expectedExceptions = ParseException.class)
    public void shouldThrowExceptionOnInvalidFormat() throws Exception{
        // given
        String dateString = "2012/04/10";
        String format = "yyyy/zz/dd";

        // when
        DateUtil.createDateFromString(dateString, format);
    }


}
