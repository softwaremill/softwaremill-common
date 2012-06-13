package pl.softwaremill.common.util.collect;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import pl.softwaremill.common.util.tuples.Pair;

import java.util.Collection;

import static com.google.common.base.Preconditions.*;
import static com.google.common.base.Predicates.*;
import static com.google.common.collect.Collections2.*;
import static pl.softwaremill.common.util.tuples.Pair.*;

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

    public static <T> Optional<T> findInstanceOf(Collection<? super T> coll, Class<T> cls) {
        for (Object o : coll) {
            if (o != null && cls.isAssignableFrom(o.getClass())) {
                //noinspection unchecked
                return Optional.of((T) o);
            }
        }

        return Optional.absent();
    }
}
