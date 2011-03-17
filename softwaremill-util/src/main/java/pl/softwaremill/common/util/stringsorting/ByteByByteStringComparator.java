package pl.softwaremill.common.util.stringsorting;

import com.google.common.primitives.UnsignedBytes;

import java.util.Comparator;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class ByteByByteStringComparator implements Comparator<String> {
    @Override
    public int compare(String o1, String o2) {
        if (o1 == null) {
            return o2 == null ? 0 : -1;
        }

        if (o2 == null) {
            return 1;
        }

        return UnsignedBytes.lexicographicalComparator().compare(o1.getBytes(), o2.getBytes());
    }
}
