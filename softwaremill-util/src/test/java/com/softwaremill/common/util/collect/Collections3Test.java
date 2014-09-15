package com.softwaremill.common.util.collect;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.testng.annotations.Test;
import com.softwaremill.common.util.tuples.Pair;

import java.util.*;

import static com.google.common.base.Predicates.alwaysFalse;
import static com.google.common.base.Predicates.alwaysTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.*;
import static com.softwaremill.common.util.collect.Collections3.maxNullSafe;
import static com.softwaremill.common.util.collect.Collections3.partition;

/**
 * @author Maciej Biłas
 * @since 9/27/11 20:13
 */
public class Collections3Test {

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

    @Test
    public void shouldFindByClass() {
        // Given
        List<Number> objects = ImmutableList.<Number>of(10L, 15, 1.4d);

        // When
        Optional<Long> result1 = Collections3.findFirstInstanceOf(objects, Long.class);
        Optional<Integer> result2 = Collections3.findFirstInstanceOf(objects, Integer.class);
        Optional<Float> result3 = Collections3.findFirstInstanceOf(objects, Float.class);

        // Then
        assertEquals(result1, Optional.of(10L));
        assertEquals(result2, Optional.of(15));
        assertEquals(result3, Optional.absent());
    }

    @Test
    public void maxNullSafeShouldReturnNullIfTheInputCollectionIsNull() {
        assertThat(maxNullSafe((Collection<String>) null)).isNull();
        assertThat(Collections3.maxNullSafe(null, null)).isNull();
    }

    @Test
    public void maxNullSafeShouldReturnNullIfTheInputCollectionIsEmpty() {
        assertThat(maxNullSafe(Collections.<String>emptySet())).isNull();
        assertThat(Collections3.maxNullSafe(Collections.<String>emptySet(), null)).isNull();
    }

    @Test
    public void maxNullSafeShouldReturnTheMaximumElementOfACollection() {
        assertThat(maxNullSafe(ImmutableList.of(1, 2, 3))).isEqualTo(3);
    }

    @Test
    public void maxNullSafeIgnoreNullCollectionElements() {
        /* I'm ignoring the method variant that takes a Comparator. There is just no point in duplicating those tests */
        assertThat(maxNullSafe(Arrays.asList(1, null, 2))).isEqualTo(2);
    }

    @Test
    public void maxNullSafeShouldReturnNullIfACollectionContainingOnlyNullsIsPassed() {
        /* Again this test is done only on the single argument variant */
        assertThat(maxNullSafe(Arrays.<Integer>asList(null, null, null))).isNull();
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
