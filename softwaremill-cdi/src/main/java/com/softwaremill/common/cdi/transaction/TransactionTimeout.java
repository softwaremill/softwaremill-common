package pl.softwaremill.common.cdi.transaction;

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;
import java.lang.annotation.*;

/**
 * Changes current trancation timeout
 * @author Adam Warski (adam at warski dot org)
 */
@Inherited
@InterceptorBinding
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TransactionTimeout {
    /**
     * New value of the transaction timeout in seconds
     *
     * @return Transaction timeout
     */
    @Nonbinding int timeout();
}

