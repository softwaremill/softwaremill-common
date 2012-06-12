package pl.softwaremill.common.util;

import com.google.common.collect.Lists;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.assertThat;

public class ObjectUtilTest {

    @Test
    public void shouldMarkNullAsNonPositive() {

        // when
        boolean positive = ObjectUtil.isPositive(null);

        // then
        assertThat(positive).isFalse();
    }

    @Test
    public void shouldMarkNullAsNonZero() {

        // when
        boolean isZero = ObjectUtil.isZero(null);

        // then
        assertThat(isZero).isFalse();
    }

    @Test(dataProvider = "dataForIsEmptyTesting")
    public void testIsEmptyMethod(Object object, boolean expectedResult) {

        // when
        boolean isEmpty = ObjectUtil.isEmpty(object);

        // then
        assertThat(isEmpty).isEqualTo(expectedResult);
    }

    @DataProvider(name = "dataForIsEmptyTesting")
    public Object[][] provideDataForIsEmptyTesting() {

        return new Object[][] {
                new Object[] {null, true },
                new Object[] {"", true },
                new Object[] {"   ", true },
                new Object[] {Lists.newArrayList(), true },
                new Object[] {new String[0], true},
                new Object[] {12L, false },
                new Object[] {"not-empty", false},
                new Object[] {Lists.newArrayList("1", "2"), false},
                new Object[] {new String[] {"abc"}, false }
        };
    }
}
