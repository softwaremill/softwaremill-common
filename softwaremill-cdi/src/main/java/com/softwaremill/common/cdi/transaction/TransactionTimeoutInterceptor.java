package com.softwaremill.common.cdi.transaction;

import javax.annotation.Resource;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.transaction.Status;
import javax.transaction.UserTransaction;
import java.io.Serializable;

/**
 * An interceptor for the {@link Transactional} annotation.
 * Must run before the {@link TransactionalInterceptor}.
 * Does not work if the tx is started before setting the timeout.
 * @author Adam Warski (adam at warski dot org)
 * @link http://smokeandice.blogspot.com/2009/12/cdi-and-declarative-transactions.html
 */
@TransactionTimeout(timeout = 5)
@Interceptor
public class TransactionTimeoutInterceptor implements Serializable {
    @Resource
    private UserTransaction utx;

    @AroundInvoke
    public Object intercept(InvocationContext ic) throws Throwable {

        // make sure the transaction is active
        if (utx.getStatus() != Status.STATUS_ACTIVE) {
            throw new RuntimeException("Transaction is not active and timeout cannot be set");
        }

        // check type
        TransactionTimeout timeout = ic.getTarget().getClass().getAnnotation(TransactionTimeout.class);

        // if not available check method
        if (timeout == null) {
            timeout = ic.getMethod().getAnnotation(TransactionTimeout.class);
        }

        // set the timeout
        utx.setTransactionTimeout(timeout.timeout());

        return ic.proceed();
    }

}
