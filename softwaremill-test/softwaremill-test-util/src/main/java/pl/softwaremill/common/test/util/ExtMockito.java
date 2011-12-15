package pl.softwaremill.common.test.util;

import org.mockito.cglib.proxy.Factory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Extensions to Mockito mocking framework.
 *
 * @author Maciej Bilas
 * @since 15/12/11 13:05
 */
public class ExtMockito {

    /**
     * Checks if the given object is a mock.
     *
     * @param o
     * @return
     * @throws NullPointerException if {@code o} is {@code null}
     */
    public static boolean isAMock(Object o) {
        return checkNotNull(o) instanceof Factory;
    }
}
