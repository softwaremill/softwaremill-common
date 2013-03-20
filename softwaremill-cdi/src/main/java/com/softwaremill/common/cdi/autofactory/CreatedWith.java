package com.softwaremill.common.cdi.autofactory;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * Creates an implementation of the specified factory class, which will return instances of the annotated class
 * when methods in the created implementation are invoked.
 *
 * The factory class should contain exactly one method, where the return type is a supertype of the annotated
 * class ((e.g. an interface implemented by the annotated class).
 *
 * The parameters of the factory method should match the arguments of the constructor.
 *
 * @author Adam Warski (adam at warski dot org)
 */
@Target({ TYPE })
@Retention(RUNTIME)
public @interface CreatedWith {
    Class<?> value();
}
