package com.softwaremill.common.test.util.fest;

import com.google.common.base.Optional;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static com.softwaremill.common.test.util.fest.OptionalConditions.absent;
import static com.softwaremill.common.test.util.fest.OptionalConditions.present;

/**
 * @author Maciej Bilas
 * @since 13/9/12 12:10
 */
public class OptionalConditionsTest {

    @Test
    public void shouldMatchAbsentOptionals() {
        assertThat(Optional.absent()).is(absent());
        assertThat(Optional.absent()).isNot(present());
    }

    @Test
    public void shouldMatchPresentOptionals() {
        assertThat(Optional.of("foo")).is(present());
        assertThat(Optional.of("foo")).isNot(absent());
    }

    @Test
    public void presentConditionShouldBeFalseOnNullObjects() {
        assertThat(present().matches(null)).isFalse();
    }

    @Test
    public void absentConditionShouldBeFalseOnNullObjects() {
        assertThat(absent().matches(null)).isFalse();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void presentConditionShouldFailOnObjectsThatAreNotOptionals() {
        present().matches("foo");
    }


    @Test(expectedExceptions = IllegalArgumentException.class)
    public void absentConditionShouldFailOnObjectsThatAreNotOptionals() {
        absent().matches("foo");
    }
}
