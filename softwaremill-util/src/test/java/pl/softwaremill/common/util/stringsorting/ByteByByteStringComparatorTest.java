package pl.softwaremill.common.util.stringsorting;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.*;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class ByteByByteStringComparatorTest {
    @Test(dataProvider="CompareSortableStringRepresentationsData")
    public void testCompareSortableStringRepresentations(String s1, String s2, int expectedResult) {
        // Given
        String rep1 = new StringToByteSortableRepresentationConverter(s1).convertWithLowercase();
        String rep2 = new StringToByteSortableRepresentationConverter(s2).convertWithLowercase();

        // When
        int result = new ByteByByteStringComparator().compare(rep1, rep2);

        // Then
        assertThat(Integer.signum(result)).isEqualTo(expectedResult);
    }

    @DataProvider(name = "CompareSortableStringRepresentationsData")
    public Object[][] getCompareSortableStringRepresentationsData() {
        return new Object[][] {
            new Object[] { "a", "a", 0 },
            new Object[] { "aaa", "a", 1 },
            new Object[] { "Ż", "ź", -1 },
            new Object[] { "źa", "źb", -1 },
            new Object[] { "£", "a", -1 },
        };
    }
}
