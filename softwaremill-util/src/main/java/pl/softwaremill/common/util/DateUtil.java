package pl.softwaremill.common.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Util class for Date manipulations
 */
public class DateUtil {

    public static final String DATE_FORMAT_YYYYMMDD = "yyyy/MM/dd";
    public static final String DATE_TIME_FORMAT_YYYYMMDD_HHSS = "yyyy/MM/dd HH:mm";

    public static Date createDateFromString(String date, String format) throws ParseException {
        if (date == null) {
            throw new ParseException("Date is null!", 0);
        }
        DateFormat df = new SimpleDateFormat(format);
        return df.parse(date);
    }

    public static String formatDate(Date date, String format) {
        DateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * Formats Date with Time  using yyyy/MM/dd HH:mm
     *
     * @return formatted Date as a String
     */
    public static String formatDateTime(Date date) {
        return formatDate(date, DATE_TIME_FORMAT_YYYYMMDD_HHSS);
    }

    /**
     * Formats Date using yyyy/MM/dd
     *
     * @return formatted Date as a String
     */

    public static String formatDate(Date date) {
        return formatDate(date, DATE_FORMAT_YYYYMMDD);
    }


}
