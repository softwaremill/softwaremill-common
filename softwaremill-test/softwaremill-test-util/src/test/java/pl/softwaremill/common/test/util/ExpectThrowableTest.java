package pl.softwaremill.common.test.util;

import org.junit.Ignore;
import org.junit.Test;

import static pl.softwaremill.common.test.util.ExpectThrowable.ExceptionMatch.*;

@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
public class ExpectThrowableTest {

  class MyTestException extends RuntimeException {
    MyTestException(String s) {
      super(s);
    }
  }

  class MyDetailedTestException extends MyTestException {
    MyDetailedTestException(String s) {
      super(s);
    }
  }

  final RuntimeException myTestException = new MyTestException("It's a me, Exception!");
  final RuntimeException myDetailedTestException = new MyDetailedTestException("It's a me, Detailed Exception!");

  Runnable throwsMyTestException = new Runnable() {
    @Override
    public void run() {
      throw myTestException;
    }
  };

  Runnable throwsMyDetailedTestException = new Runnable() {
    @Override
    public void run() {
      throw myDetailedTestException;
    }
  };

  @Test
  public void thrown_shouldInterceptExactException() throws Exception {
    // when
    ExpectThrowable.thrown(MyTestException.class, throwsMyTestException);

    // then, the exception should be swallowed
  }

  @Test
  public void thrown_shouldNotInterceptSubClassExceptionThrown() throws Exception {
    // when
    ExpectThrowable.thrown(MyTestException.class, throwsMyDetailedTestException);

    // then
    Assert.fail("It should not have swallowed a sub class exception.");
  }

  @Test
  public void thrown_EXCEPTION_MAY_BE_SUBCLASS_OF_shouldInterceptSubClassException() throws Exception {
    // when
    ExpectThrowable.thrown(EXCEPTION_MAY_BE_SUBCLASS_OF, MyTestException.class, throwsMyDetailedTestException);

    // then, should have cought sub class exception
  }

  @Test
  public void intercept_shouldInterceptExactException() throws Exception {
    // when
    RuntimeException intercepted = ExpectThrowable.intercept(MyTestException.class, throwsMyTestException);

    // then
    assertThat(intercepted).isEqualTo(myTestException);
  }

  @Test
  public void intercept_shouldNotInterceptSubClassException() throws Exception {
    // when
    RuntimeException intercepted = ExpectThrowable.intercept(MyTestException.class, throwsMyDetailedTestException);

    // then
    Assert.fail("Should not have swallowed sub class exception.");
  }

  @Test
  public void intercept_EXCEPTION_MAY_BE_SUBCLASS_OF_shouldInterceptSubClassException() throws Exception {
    // when
    MyTestException intercepted = ExpectThrowable.intercept(EXCEPTION_MAY_BE_SUBCLASS_OF, MyTestException.class, throwsMyDetailedTestException);

    // then
    assertThat(intercepted).isEqualTo(myDetailedTestException);
  }
}
