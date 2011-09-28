package pl.softwaremill.common.util.collect;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.testng.annotations.Test;
import pl.softwaremill.common.util.tuples.Pair;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import static com.google.common.base.Predicates.alwaysFalse;
import static com.google.common.base.Predicates.alwaysTrue;
import static org.testng.Assert.*;
import static pl.softwaremill.common.util.collect.SMLCollections.partition;

/**
 * @author Maciej Biłas
 * @since 9/27/11 20:13
 */
public class SMLCollectionsTest {

    @SuppressWarnings({"NullableProblems"})
    @Test(expectedExceptions = NullPointerException.class)
    public void partitionShouldThrowNPEIfCollectionIsNull() {
        partition(null, Predicates.<Object>alwaysTrue());
    }

    @SuppressWarnings({"NullableProblems"})
    @Test(expectedExceptions = NullPointerException.class)
    public void partitionShouldThrowNPEIfPredicateIsNull() {
        partition(ImmutableList.of(1, 2, 3), null);
    }

    @Test
    public void partitionShouldReturnAPairOfEmptyCollectionsIfEmptyCollectionIsPassed() {
        Pair<Collection<Object>, Collection<Object>> partitioned =
                partition(Collections.emptySet(), alwaysTrue());

        assertNotNull(partitioned);
        assertEmpty(partitioned.getLeft());
        assertEmpty(partitioned.getRight());
    }

    @Test
    public void partitionShouldReturnAnEmptyRightCollectionIfAllElementsSatisfyThePredicate() {
        Pair<Collection<Integer>, Collection<Integer>> collections =
                partition(ImmutableList.of(1, 2), alwaysTrue());

        Collection<Integer> left = collections.getLeft();
        assertContainsExactly(left, ImmutableSet.of(1, 2));

        Collection<Integer> right = collections.getRight();
        assertEmpty(right);
    }

    @Test
    public void partitionShouldReturnAnEmptyLeftCollectionIfNoElementsSatisfyThePredicate() {
        Pair<Collection<Integer>, Collection<Integer>> collections =
                partition(ImmutableList.of(1, 2), alwaysFalse());

        Collection<Integer> left = collections.getLeft();
        assertEmpty(left);

        Collection<Integer> right = collections.getRight();
        assertContainsExactly(right, ImmutableList.of(1, 2));
    }

    @Test
    public void partitionShouldReturnPartitionedCollectionsFor4IntCollection() {
        Pair<Collection<Integer>, Collection<Integer>> collections =
                partition(ImmutableList.of(1, 2, 3, 4), new Predicate<Integer>() {
                    @Override
                    public boolean apply(Integer input) {
                        return input > 2;
                    }
                });
        Set<Integer> satisfied = ImmutableSet.copyOf(collections.getLeft());
        Set<Integer> unsatisfied = ImmutableSet.copyOf(collections.getRight());

        assertNotNull(unsatisfied);
        assertEquals(unsatisfied.size(), 2);
        assertTrue(unsatisfied.containsAll(ImmutableSet.of(1, 2)), "Right set should contain elements 1 and 2.");

        assertNotNull(satisfied);
        assertEquals(satisfied.size(), 2);
        assertTrue(satisfied.containsAll(ImmutableSet.of(3, 4)), "Left set should contain elements 3 and 4.");
    }

    private <E> void assertContainsExactly(Collection<E> left, Collection<E> elements) {
        assertNotNull(left);
        assertEquals(left.size(), elements.size());
        assertTrue(left.containsAll(elements));
    }

    private <E> void assertEmpty(Collection<E> collection) {
        assertNotNull(collection);
        assertTrue(collection.isEmpty());
    }
}
