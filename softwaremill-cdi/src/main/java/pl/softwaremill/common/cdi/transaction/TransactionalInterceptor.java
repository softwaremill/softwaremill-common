package pl.softwaremill.common.cdi.transaction;

import javax.annotation.Resource;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.transaction.Status;
import javax.transaction.UserTransaction;
import java.io.Serializable;

/**
 * An interceptor for the {@link pl.softwaremill.common.cdi.transaction.Transactional} annotation.
 * @author Adam Warski (adam at warski dot org)
 * @link http://smokeandice.blogspot.com/2009/12/cdi-and-declarative-transactions.html
 */
@Transactional
@Interceptor
public class TransactionalInterceptor implements Serializable {
    @Resource
    private UserTransaction utx;

    @AroundInvoke
    public Object intercept(InvocationContext ic) throws Throwable {
        boolean startedTransaction = false;
        if (utx.getStatus() != Status.STATUS_ACTIVE) {
            utx.begin();
            startedTransaction = true;
        }

        Object ret;
        try {
            ret = ic.proceed();

            if (startedTransaction){
                utx.commit();
            }
        } catch(Throwable t) {
            if (startedTransaction) {
                utx.rollback();
            }

            throw t;
        }

        return ret;
    }

}
