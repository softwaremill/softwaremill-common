package pl.softwaremill.common.cdi.security;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * Allows access when any of the given security flags are set.
 * Has higher priority than restrictions specified with @{@link Secure}, that is when access is granted basing
 * on security flags, other restrictions are not checked.
 * @author Adam Warski (adam at warski dot org)
 */
@Retention(RUNTIME)
@Target({TYPE, METHOD})
public @interface AllowWithFlag {
    String[]    value();
}
