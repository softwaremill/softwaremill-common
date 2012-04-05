package pl.softwaremill.common.dbtest.util;

import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.ApplicationScoped;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.test.spi.event.suite.BeforeSuite;

import javax.naming.Context;
import javax.naming.NamingException;

/**
 * User: szimano
 */
public class InMemoryContextProducer {

    @Inject @ApplicationScoped
    private InstanceProducer<Context> contextProducer;

    public void produceNamingContext(@Observes BeforeSuite beforeSuiteEvent) {
        try {
            contextProducer.set(new InMemoryContextFactory().getInitialContext(null));
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }
}
