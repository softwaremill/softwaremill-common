package pl.softwaremill.common.cdi.objectservice.auto;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Used to mark implementation of Object Service.
 */
@Qualifier
@Target({ TYPE })
@Retention(RUNTIME)
public @interface OSImpl {
}
