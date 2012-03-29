package pl.softwaremill.common.dbtest.util;

import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.InitialContextFactoryBuilder;
import java.util.Hashtable;

/**
 * User: szimano
 */
public class InMemoryContextFactoryBuilder implements InitialContextFactoryBuilder{

    @Override
    public InitialContextFactory createInitialContextFactory(Hashtable<?, ?> hashtable) throws NamingException {
        return new InMemoryContextFactory();
    }
}
