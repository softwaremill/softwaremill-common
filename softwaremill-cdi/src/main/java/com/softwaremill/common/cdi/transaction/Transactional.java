package com.softwaremill.common.cdi.transaction;

import javax.interceptor.InterceptorBinding;
import java.lang.annotation.*;

/**
 * Surrounds a method call with a transaction, if one is not yet running.
 * @author Adam Warski (adam at warski dot org)
 */
@Inherited
@InterceptorBinding
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Transactional {
}

