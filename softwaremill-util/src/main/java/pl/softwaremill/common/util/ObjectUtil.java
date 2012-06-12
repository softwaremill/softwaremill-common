package pl.softwaremill.common.util;

import java.lang.reflect.Array;
import java.util.Collection;

/**
 * Object-based util methods
 */
public class ObjectUtil {

    /**
     * Checks if an object is empty taking into consideration regular objects, collections etc. Null means empty.
     *
     * @param o Object to check.
     * @return True if empty
     */
    public static boolean isEmpty(Object o) {

        // if it's null, it means it's empty
        if (o == null) {
            return true;
        }

        // if string make sure it's not 0 length even after trimming
        if (o instanceof String) {
            return ((String) o).trim().length() == 0;
        }
        // for arrays check number of elements
        else if (o.getClass().isArray()) {
            return Array.getLength(o) == 0;
        }
        // if it's a collection check isEmpty
        else if (o instanceof Collection) {
            return ((Collection) o).isEmpty();
        }

        // finally we can assume it's not empty
        return false;
    }

    public static boolean isNotEmpty(Object o) {
        return !isEmpty(o);
    }

    public static boolean isZero(Long number) {
        return number != null && number == 0;
    }

    public static boolean isPositive(Long number) {
        return number != null && number > 0;
    }
}
