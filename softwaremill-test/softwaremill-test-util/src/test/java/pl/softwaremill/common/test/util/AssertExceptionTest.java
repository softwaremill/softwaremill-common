package pl.softwaremill.common.test.util;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;
import static pl.softwaremill.common.test.util.AssertException.ExceptionMatch.EXCEPTION_MAY_BE_SUBCLASS_OF;

/**
 * Ironic test in which we check the "better way" of checking exceptions
 * by writing tests "the old way".
 *
 * @author Konrad Malawski (konrad.malawski@java.pl)
 */
@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
public class AssertExceptionTest {

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

    final RuntimeException myTestException = new MyTestException("It's a me, Exception!"); // Super Mario pun
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
        AssertException.thrown(MyTestException.class, throwsMyTestException);

        // then, the exception should be swallowed
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = "Expected .MyTestException. to be thrown but got .MyDetailedTestException.")
    public void thrown_shouldNotInterceptSubClassExceptionThrown() throws Exception {
        // when
        AssertException.thrown(MyTestException.class, throwsMyDetailedTestException);

        // then
        fail("It should not have swallowed a sub class exception.");
    }

    @Test
    public void thrown_EXCEPTION_MAY_BE_SUBCLASS_OF_shouldInterceptSubClassException() throws Exception {
        // when
        AssertException.thrown(EXCEPTION_MAY_BE_SUBCLASS_OF, MyTestException.class, throwsMyDetailedTestException);

        // then, should have cought sub class exception
    }

    @Test
    public void intercept_shouldInterceptExactException() throws Exception {
        // when
        RuntimeException intercepted = AssertException.intercept(MyTestException.class, throwsMyTestException);

        // then
        assertEquals(intercepted, myTestException);
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = "Expected .MyTestException. to be thrown but got .MyDetailedTestException.")
    public void intercept_shouldNotInterceptSubClassException() throws Exception {
        // when
        RuntimeException intercepted = AssertException.intercept(MyTestException.class, throwsMyDetailedTestException);

        // then
        fail("Should not have swallowed sub class exception.");
    }

    @Test
    public void intercept_EXCEPTION_MAY_BE_SUBCLASS_OF_shouldInterceptSubClassException() throws Exception {
        // when
        MyTestException intercepted = AssertException.intercept(EXCEPTION_MAY_BE_SUBCLASS_OF, MyTestException.class, throwsMyDetailedTestException);

        // then
        assertEquals(intercepted, myDetailedTestException);
    }
}
