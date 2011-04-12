package pl.softwaremill.common.cdi.autofactory.instance;

import pl.softwaremill.common.cdi.autofactory.CreatedWith;
import pl.softwaremill.common.cdi.autofactory.FactoryParameter;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

/**
 * @author Adam Warski (adam at warski dot org)
 */
@CreatedWith(InjectDataAndInstanceConstructor.Factory.class)
public class InjectDataAndInstanceConstructor {
    private final String data;
    private final Instance<InstanceBean> bean;
    private final Instance<InstanceBean2> bean2;

    @Inject
    public InjectDataAndInstanceConstructor(Instance<InstanceBean> bean, Instance<InstanceBean2> bean2, @FactoryParameter String data) {
        this.bean = bean;
        this.bean2 = bean2;
        this.data = data;
    }

    public String getData() {
        return data + bean.get().getData() + bean2.get().getData();
    }

    public static interface Factory {
        InjectDataAndInstanceConstructor create(String data);
    }
}
