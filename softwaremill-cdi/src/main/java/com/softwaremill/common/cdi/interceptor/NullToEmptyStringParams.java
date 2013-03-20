package com.softwaremill.common.cdi.interceptor;

import javax.interceptor.InterceptorBinding;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * Method annotated with @NullToEmptyStringParams will never receive null values for String parameters.
 * Parameter values will be converted to empty Strings instead.
 *
 * @author Pawel Wrzeszcz (pawel [at] softwaremill . com)
 */
@InterceptorBinding
@Target({METHOD, TYPE})
@Retention(RUNTIME)
public @interface NullToEmptyStringParams {}
