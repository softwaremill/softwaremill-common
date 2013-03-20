package com.softwaremill.common.util.tuples;

import com.google.common.collect.Ordering;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class LexicographicPair<A extends Comparable, B extends Comparable>
        extends Pair<A, B>
        implements Comparable<LexicographicPair<A, B>> {
    private final static Ordering ordering = Ordering.natural().nullsFirst();

    private LexicographicPair(A left, B right) {
        super(left, right);
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public int compareTo(LexicographicPair<A, B> other) {
        int result = ordering.compare(getLeft(), other.getLeft());
        if (result == 0) {
            return ordering.compare(getRight(), other.getRight());
        } else {
            return result;
        }
    }

    public static <A extends Comparable<A>, B extends Comparable<B>> LexicographicPair<A, B> pair(A left, B right) {
        return new LexicographicPair<A, B>(left, right);
    }
}
