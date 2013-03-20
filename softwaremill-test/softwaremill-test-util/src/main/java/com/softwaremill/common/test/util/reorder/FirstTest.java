package com.softwaremill.common.test.util.reorder;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marker to move method or class as a first to run.
 *
 * 1. It moves method to be run as a first in its class.
 * It can not be used with dependsOnMethods parameter (from @Test) as in such situation TestNG will execute method
 * specified in this parameter before one marked with @FirstTest.
 *
 * 2. It moves class to be run as a first among not depending upon other test classes
 *
 * Beware that not all methods and classes can be reordered, only those which don't have any methods depended upon
 * (for more see javadoc for org.testng.IMethodInterceptor)
 *
 * @author Tomasz Dziurko
 */
@Target({METHOD, TYPE})
@Retention(RUNTIME)
public @interface FirstTest {
}
