package pl.softwaremill.common.cdi.autofactory.extension.parameter;

import javax.enterprise.inject.spi.BeanManager;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class FactoryParameterParameterValue implements ParameterValue {
    private final int idx;

    public FactoryParameterParameterValue(int idx) {
        this.idx = idx;
    }


    @Override
    public Object getValue(BeanManager bm, Object[] factoryParameters) {
        return factoryParameters[idx];
    }
}
