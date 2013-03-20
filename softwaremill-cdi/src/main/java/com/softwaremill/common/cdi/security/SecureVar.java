package com.softwaremill.common.cdi.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Adam Warski (adam at warski dot org)
 */
@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface SecureVar {
    /**
     * @return Name of the variable, which will be available when checking security constraints.
     */
    String  value();

    /**
     * @return An optional EL expression which will be evaluated to get the value of the variable. The name
     * of the paramter during the evaluation is p, so the expression can be e.g.: #{p.attribute}.
     */
    String  exp() default "";
}
