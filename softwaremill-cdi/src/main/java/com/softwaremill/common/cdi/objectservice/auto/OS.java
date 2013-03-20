package pl.softwaremill.common.cdi.objectservice.auto;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Used to mark interface for Object Service
 */
@Target({ TYPE })
@Retention(RUNTIME)
public @interface OS {
}
