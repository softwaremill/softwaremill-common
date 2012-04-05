package pl.softwaremill.common.dbtest.util;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * User: szimano
 */
public class InMemoryContextFactory implements InitialContextFactory{

    private final static ThreadLocal<Context> contextThreadLocal = new ThreadLocal<Context>();

    @Override
    public Context getInitialContext(Hashtable<?, ?> hashtable) throws NamingException {
        Context context;

        if ((context = contextThreadLocal.get()) == null) {
            context = new InitialContext(true) {
                Map<String, Object> bindings = new HashMap<String, Object>();

                @Override
                public void bind(String name, Object obj)
                        throws NamingException {
                    bindings.put(name, obj);
                }

                @Override
                public Object lookup(String name) throws NamingException {
                    return bindings.get(name);
                }
            };

            contextThreadLocal.set(context);
        }

        return context;
    }
}
