package pl.softwaremill.common.cdi.objectservice;

import javax.enterprise.context.RequestScoped;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A service for which implementations are scoped beans, with instance counters.
 * @author Adam Warski (adam at warski dot org)
 */
@RequestScoped
public class Service2C implements Service2<C> {
    private C serviced;

    public Service2C() {
        instanceCounter.incrementAndGet();
    }

    @Override
    public void setServiced(C serviced) {
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
