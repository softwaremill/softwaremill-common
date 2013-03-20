package com.softwaremill.common.cdi.autofactory;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * Specifies that the annotated constructor parameter should map to a parameter of the factory method.
 *
 * @author Adam Warski (adam at warski dot org)
 */
@Target({ PARAMETER })
@Retention(RUNTIME)
public @interface FactoryParameter {
}
