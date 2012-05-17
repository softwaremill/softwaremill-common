package pl.softwaremill.common.cdi.config;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Mark simple any class as configuration class
 * User: krzysztof.grajek 'at' googlemail.com
 * Date: 17/05/2012
 */
@Retention(RUNTIME)
@Target({TYPE})
public @interface Configuration {
}

