package pl.softwaremill.common.test.util.reorder;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marker to define order in which test methods in class should be run
 *
 * Beware that not all methods can be reordered, only those which don't have any methods depended upon
 * (for more see javadoc for org.testng.IMethodInterceptor)
 *
 * @author Tomasz Dziurko
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface TestOrder {

    int order() default 0;

}
