package pl.softwaremill.common.cdi.objectservice;

import javax.enterprise.context.ApplicationScoped;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A service for which implementations are scoped beans, with instance counters.
 * @author Adam Warski (adam at warski dot org)
 */
@ApplicationScoped
public class Service2B implements Service2<B> {
    private B serviced;

    public Service2B() {
        instanceCounter.incrementAndGet();
    }

    @Override
    public void setServiced(B serviced) {
        this.serviced = serviced;
    }

    @Override
    public Object get() {
        return serviced.getValue();
    }

    private static AtomicInteger instanceCounter = new AtomicInteger();

    public static int instanceCount() {
        return instanceCounter.get();
    }
}
