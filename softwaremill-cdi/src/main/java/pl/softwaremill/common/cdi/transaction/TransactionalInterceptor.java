package pl.softwaremill.common.cdi.transaction;

import org.slf4j.Logger;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.transaction.Status;
import javax.transaction.UserTransaction;
import java.io.Serializable;

/**
 * An interceptor for the {@link pl.softwaremill.common.cdi.transaction.Transactional} annotation.
 *
 * @author Adam Warski (adam at warski dot org)
 * @link http://smokeandice.blogspot.com/2009/12/cdi-and-declarative-transactions.html
 */
@Transactional
@Interceptor
public class TransactionalInterceptor implements Serializable {

    @Inject
    private Logger logger;

    @Resource
    private UserTransaction utx;

    @AroundInvoke
    public Object intercept(InvocationContext ic) throws Throwable {
        boolean startedTransaction = false;
        if (utx.getStatus() != Status.STATUS_ACTIVE) {
            logger.debug("Starting a new transaction!");
            utx.begin();
            startedTransaction = true;
        }

        Object ret;
        try {
            ret = ic.proceed();

            if (startedTransaction) {
                logger.debug("Transaction was executed properly!");
                utx.commit();
            }
        } catch (Throwable t) {
            if (utx.getStatus() == Status.STATUS_ACTIVE) {
                logger.debug("Rolling back transaction!");
                utx.rollback();
            }

            throw t;
        }

        return ret;
    }

}
