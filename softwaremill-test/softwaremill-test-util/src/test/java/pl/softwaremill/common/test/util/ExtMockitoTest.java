package pl.softwaremill.common.test.util;

import org.testng.annotations.Test;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static pl.softwaremill.common.test.util.ExtMockito.isAMock;

/**
 * @author Maciej Bilas
 * @since 15/12/11 13:06
 */
public class ExtMockitoTest {

    private static final List A_MOCK = mock(List.class);
    private static final int NOT_A_MOCK = 1;

    @Test
    public void isAMockShouldReturnTrueWhenAMockIsPassedToIt() {
        assertTrue(isAMock(A_MOCK));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void isAMockShouldThrowAnNPEIfTheObjectPassedToItIsNull() {
        isAMock(null);
    }

    @Test
    public void isAMockShouldReturnFalseIfTheObjectIsNotAMock() {
        assertFalse(isAMock(NOT_A_MOCK));
    }
}
