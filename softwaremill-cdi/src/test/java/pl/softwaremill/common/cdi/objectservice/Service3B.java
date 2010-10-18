package pl.softwaremill.common.cdi.objectservice;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class Service3B extends AbstractService3<B> {
    private B serviced;

    @Override
    public void setServiced(B serviced) {
        this.serviced = serviced;
    }

    @Override
    public Object get() {
        return serviced.getValue();
    }
}
