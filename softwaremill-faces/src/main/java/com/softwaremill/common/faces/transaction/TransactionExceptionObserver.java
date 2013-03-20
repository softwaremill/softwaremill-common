package pl.softwaremill.common.faces.transaction;

import javax.annotation.Resource;
import javax.enterprise.event.Observes;
import javax.faces.event.ExceptionQueuedEvent;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class TransactionExceptionObserver {
    @Resource
    private UserTransaction utx;

    /**
     * When an exception is thrown, the transaction is rolled back. 
     * @param e The event.
     */
    public void onExceptionQueued(@Observes ExceptionQueuedEvent e) {
        try {
            if (utx.getStatus() == Status.STATUS_ACTIVE) {
                utx.rollback();
            }
        } catch (SystemException e1) {
            throw new RuntimeException(e1);
        }
    }
}
