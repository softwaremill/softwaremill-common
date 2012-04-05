package pl.softwaremill.common.dbtest.util;

import javax.transaction.*;

/**
 * User: szimano
 */
public class DBTestUserTransaction implements UserTransaction {
    private TransactionManager delegate;

    public DBTestUserTransaction(TransactionManager delegate) {
        this.delegate = delegate;
    }

    @Override
    public void begin() throws NotSupportedException, SystemException {
        delegate.begin();
    }

    @Override
    public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException, SystemException {
        delegate.commit();
    }

    @Override
    public void rollback() throws IllegalStateException, SecurityException, SystemException {
        delegate.rollback();
    }

    @Override
    public void setRollbackOnly() throws IllegalStateException, SystemException {
        delegate.setRollbackOnly();
    }

    @Override
    public int getStatus() throws SystemException {
        return delegate.getStatus();
    }

    @Override
    public void setTransactionTimeout(int seconds) throws SystemException {
        delegate.setTransactionTimeout(seconds);
    }
}
