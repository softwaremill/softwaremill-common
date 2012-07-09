package pl.softwaremill.common.util.collect;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import pl.softwaremill.common.util.tuples.Pair;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.not;
import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.Collections2.filter;
import static pl.softwaremill.common.util.tuples.Pair.pair;

/**
 * Common {@link java.util.Collection} helper methods
 *
 * @author Maciej Biłas
 * @since 9/27/11 19:41
 */
public class Collections3 {

    private Collections3() {
        /* this class should not be instantiated */
    }

    /**
     * Partitions a {@link Collection} according to the predicate.
     *
     * @param unpartitioned the unpartitioned collection
     * @param predicate     the predicate
     * @param <E>           type of collection's elements
     * @return a pair of collections. The left element is satisfying the predicate, the right -- not.
     * @throws NullPointerException if any of the arguments is {@code null}.
     */
    public static <E> Pair<Collection<E>, Collection<E>> partition(Collection<E> unpartitioned,
                                                                   Predicate<? super E> predicate) {
        return pair(filter(checkNotNull(unpartitioned), checkNotNull(predicate)),
                filter(unpartitioned, not(predicate)));
    }

    public static <T> Optional<T> findFirstInstanceOf(Collection<? super T> coll, Class<T> cls) {
        for (Object o : coll) {
            if (o != null && cls.isAssignableFrom(o.getClass())) {
                //noinspection unchecked
                return Optional.of((T) o);
            }
        }

        return Optional.absent();
    }

    /**
     * Returns the maximum element of the given collection, according to the
     * order induced by the specified comparator.
     * This method will neither throw {@link NullPointerException}
     * nor {@link java.util.NoSuchElementException} if the provided collection is {@code null}
     * or empty. In addition {@code null} elements are filtered from the collection.
     *
     * @param coll the collection whose maximum element is to be determined.
     * @param comp the comparator with which to determine the maximum element.
     *             A <tt>null</tt> value indicates that the elements' <i>natural
     *             ordering</i> should be used.
     * @return the maximum element of the given collection, according
     *         to the specified comparator or {@code null}
     *         if the input collection is {@code null} or empty.
     * @throws ClassCastException if the collection contains elements that are
     *                            not <i>mutually comparable</i> using the specified comparator.
     * @see Comparable
     */
    public static <T> T nullSafeMax(Collection<? extends T> coll, Comparator<? super T> comp) {
        if (coll == null || coll.isEmpty())
            return null;

        return Collections.max(filter(coll, notNull()), comp);
    }

    /**
     * Returns the maximum element of the given collection, according to the
     * <i>natural ordering</i> of its elements.
     * This method will neither throw {@link NullPointerException}
     * nor {@link java.util.NoSuchElementException} if the provided collection is {@code null}
     * or empty. In addition {@code null} elements are filtered from the collection.
     *
     * @param coll the collection whose maximum element is to be determined.
     * @return the maximum element of the given collection, according
     *         to the <i>natural ordering</i> of its elements or {@code null}
     *         if the input collection is {@code null} or empty.
     * @throws ClassCastException if the collection contains elements that are
     *                            not <i>mutually comparable</i> (for example, strings and
     *                            integers).
     * @see Comparable
     */
    public static <T extends Object & Comparable<? super T>> T nullSafeMax(Collection<? extends T> coll) {
        if (coll == null || coll.isEmpty())
            return null;

        return Collections.max(filter(coll, notNull()));
    }
}
