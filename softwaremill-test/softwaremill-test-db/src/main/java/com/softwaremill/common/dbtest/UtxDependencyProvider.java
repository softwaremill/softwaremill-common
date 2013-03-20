package pl.softwaremill.common.dbtest;

import bitronix.tm.TransactionManagerServices;
import pl.softwaremill.common.util.dependency.DependencyProvider;

import javax.transaction.UserTransaction;
import java.lang.annotation.Annotation;

/**
* Provides wrapping class to inject UserTransaction
*/
public class UtxDependencyProvider implements DependencyProvider {

    @Override
    public <T> T inject(Class<T> cls, Annotation... qualifiers) {
        if (cls.isAssignableFrom(UserTransaction.class)) {
            return (T) TransactionManagerServices.getTransactionManager();
        }
        return null;
    }

}
