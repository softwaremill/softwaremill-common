package com.softwaremill.common.cdi.security;

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * Checks a condition for a method's return value (post-condition).
 * TODO: add an internationalized message
 * @author Adam Warski (adam at warski dot org)
 */
@Retention(RUNTIME)
@Target({METHOD, TYPE})
@InterceptorBinding
public @interface SecureResult {
    /**
     * @return The EL expression that should be evaluated. If it evaluates to {@code true}, access will be granted.
     * The EL expression may reference any objects that are in any context, as well as the result of the method under
     * the name {@code result}.
     */
    @Nonbinding
    String  value();
}