package pl.softwaremill.common.test.util;

import org.testng.Assert;

import static pl.softwaremill.common.test.util.AssertException.ExceptionMatch.EXCEPTION_MUST_EQUAL;

/**
 * Allows expecting and intercepting exceptions in a nice way.
 * Use it to intercept exceptions in your tests, in a way that allows
 * sticking to the given/when/then flow, and validate exception throws on
 * <p/>
 * SoftwareBirr 02.2012
 *
 * @author Konrad Malawski (konrad.malawski@java.pl)
 */
public class AssertException {

    public static abstract class ExceptionMatch {

        public static final ExceptionMatch.Strategy EXCEPTION_MUST_EQUAL = new Strategy() {
            @Override
            public boolean matchesExpected(Class<?> expected, Class<? extends Throwable> got) {
                return got.equals(expected);
            }
        };

        public static final ExceptionMatch.Strategy EXCEPTION_MAY_BE_SUBCLASS_OF = new Strategy() {
            @Override
            public boolean matchesExpected(Class<?> expected, Class<? extends Throwable> got) {
                return expected.isAssignableFrom(got);
            }
        };

        static interface Strategy {
            boolean matchesExpected(Class<?> expected, Class<? extends Throwable> got);
        }
    }

    public static <T extends Throwable> void thrown(ExceptionMatch.Strategy matchStrategy,
                                                    Class<T> expectedThrowableClass,
                                                    Runnable block) {
        try {
            block.run();

            failWithExpectedButGotNothing(expectedThrowableClass);

        } catch (Throwable thr) {
            Class<? extends Throwable> gotThrowableClass = thr.getClass();

            boolean gotExpectedException = matchStrategy.matchesExpected(expectedThrowableClass, gotThrowableClass);
            if (!gotExpectedException) {
                failWithExpectedButGot(expectedThrowableClass, gotThrowableClass);
            }
        }
    }

    public static <T extends Throwable> void thrown(Class<T> expectedThrowableClass,
                                                    Runnable block) {
        thrown(EXCEPTION_MUST_EQUAL, expectedThrowableClass, block);
    }

    public static <T extends Throwable> T intercept(Class<T> expectedThrowableClass,
                                                    Runnable block) {
        return intercept(EXCEPTION_MUST_EQUAL, expectedThrowableClass, block);
    }

    public static <T extends Throwable> T intercept(ExceptionMatch.Strategy matchStrategy,
                                                    Class<T> expectedThrowableClass,
                                                    Runnable block) {
        try {
            block.run();

            failWithExpectedButGotNothing(expectedThrowableClass); // will throw
            return null; // make compiler happy

        } catch (Throwable thr) {
            Class<? extends Throwable> gotThrowableClass = thr.getClass();

            boolean gotExpectedException = matchStrategy.matchesExpected(expectedThrowableClass, gotThrowableClass);
            if (gotExpectedException) {
                return expectedThrowableClass.cast(thr);
            } else {
                failWithExpectedButGot(expectedThrowableClass, gotThrowableClass); // will throw
                return null; // make compiler happy
            }
        }
    }

    private static void failWithExpectedButGotNothing(Class<?> expected) {
        Assert.fail(String.format("Expected [%s] to be thrown but no exception was thrown.", expected.getSimpleName()));
    }

    private static void failWithExpectedButGot(Class<?> expected, Class<?> got) {
        Assert.fail(String.format("Expected [%s] to be thrown but got [%s]", expected.getSimpleName(), got.getSimpleName()));
    }

}
