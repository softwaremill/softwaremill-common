package pl.softwaremill.common.cdi.objectservice;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class Service3C extends AbstractService3<C>  {
    private C serviced;

    @Override
    public void setServiced(C serviced) {
        this.serviced = serviced;
    }

    @Override
    public Object get() {
        return serviced.getValue();
    }
}
