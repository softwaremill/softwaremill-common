package pl.softwaremill.common.test.util;

import com.sun.istack.internal.NotNull;
import sun.jvm.hotspot.utilities.Assert;

import static pl.softwaremill.common.test.util.ExpectThrowable.ExceptionMatch.EXCEPTION_MUST_EQUAL;

/**
 * Allows expecting and intercepting exceptions in a nice way.
 * Use it to intercept exceptions in your tests, in a way that allows
 * sticking to the given/when/then flow, and validate exception throws on
 * <p/>
 * SoftwareBirr 02.2012
 *
 * @author Konrad Malawski (konrad.malawski@java.pl)
 */
public class ExpectThrowable {

  public static abstract class ExceptionMatch {

    public static final ExceptionMatch.Strategy EXCEPTION_MUST_EQUAL = new Strategy() {
      @Override
      public boolean matchesExpected(@NotNull Class<?> expected, @NotNull Class<? extends Throwable> got) {
        return got.equals(expected);
      }
    };

    public static final ExceptionMatch.Strategy EXCEPTION_MAY_BE_SUBCLASS_OF = new Strategy() {
      @Override
      public boolean matchesExpected(@NotNull Class<?> expected, @NotNull Class<? extends Throwable> got) {
        return got.isAssignableFrom(expected); // todo check this
      }
    };

    static interface Strategy {
      boolean matchesExpected(@NotNull Class<?> expected, @NotNull Class<? extends Throwable> got);
    }
  }

  public static <T extends Throwable> void thrown(@NotNull ExceptionMatch.Strategy matchStrategy,
                                                  @NotNull Class<T> expectedThrowableClass,
                                                  @NotNull Runnable block) {
    try {
      block.run();

      failWithExpectedButGotNothing(expectedThrowableClass);

    } catch (Throwable thr) {
      Class<? extends Throwable> thrownClass = thr.getClass();

      boolean gotExpectedException = matchStrategy.matchesExpected(expectedThrowableClass, thrownClass);
      if (!gotExpectedException) {
        failWithExpectedButGot(expectedThrowableClass, thrownClass);
      }
    }
  }

  public static <T extends Throwable> void thrown(@NotNull Class<T> expectedThrowableClass,
                                                  @NotNull Runnable block) {
    thrown(EXCEPTION_MUST_EQUAL, expectedThrowableClass, block);
  }

  public static <T extends Throwable> T intercept(@NotNull Class<T> expectedThrowableClass,
                                                  @NotNull Runnable block) {
    return intercept(EXCEPTION_MUST_EQUAL, expectedThrowableClass, block);
  }

  public static <T extends Throwable> T intercept(@NotNull ExceptionMatch.Strategy matchStrategy,
                                                  @NotNull Class<T> expectedThrowableClass,
                                                  @NotNull Runnable block) {
    try {
      block.run();

      failWithExpectedButGotNothing(expectedThrowableClass); // will throw
      return null; // make compiler happy

    } catch (Throwable thr) {
      Class<? extends Throwable> thrownClass = thr.getClass();

      boolean gotExpectedException = thrownClass.equals(expectedThrowableClass);
      if (gotExpectedException) {
        return expectedThrowableClass.cast(thr);
      } else {
        failWithExpectedButGot(expectedThrowableClass, expectedThrowableClass); // will throw
        return null; // make compiler happy
      }
    }
  }

  private static void failWithExpectedButGotNothing(Class<?> expected) throws AssertionException {
    Assert.fail(String.format("Expected [%s] to be thrown but no exception was thrown.", expected.getSimpleName()));
  }

  private static void failWithExpectedButGot(Class<?> expected, Class<?> got) throws AssertionException {
    Assert.fail(String.format("Expected [%s] to be thrown but got [%s]", expected.getSimpleName(), got.getSimpleName()));
  }

}
