package pl.softwaremill.common.util;

import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Util class for Date manipulations
 */
public class DateUtil {

    public static final String DATE_FORMAT_YYYYMMDD = "yyyy/MM/dd";
    public static final String DATE_TIME_FORMAT_YYYYMMDD_HHMM = "yyyy/MM/dd HH:mm";

    public static Date createDateFromString(String date, String format) throws ParseException {
        if (date == null) {
            throw new IllegalArgumentException("Date is null!");
        }
        DateFormat df = new SimpleDateFormat(format);
        return df.parse(date);
    }

    public static String formatDate(Date date, String format) {
        DateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * Formats Date with Time using yyyy/MM/dd HH:mm
     *
     * @return formatted Date as a String
     */
    public static String formatDateTime(Date date) {
        return formatDate(date, DATE_TIME_FORMAT_YYYYMMDD_HHMM);
    }

    /**
     * Formats Date using yyyy/MM/dd
     *
     * @return formatted Date as a String
     */

    public static String formatDate(Date date) {
        return formatDate(date, DATE_FORMAT_YYYYMMDD);
    }

    /**
     * Created date with given year, month and day
     *
     * @param year  given year
     * @param month given month (1-12)
     * @param day   given day of month
     * @return created Date
     */
    public static Date createDate(int year, int month, int day) {
        return new DateTime().withDate(year, month, day).toDate();
    }


}
