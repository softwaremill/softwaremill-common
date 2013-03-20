package pl.softwaremill.common.cdi.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A meta-annotations which should be placed on annotations that specify security constraints using @{@link Secure},
 * or other secure binding annotations.
 * @author Adam Warski (adam at warski dot org)
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface SecureBinding {
}

