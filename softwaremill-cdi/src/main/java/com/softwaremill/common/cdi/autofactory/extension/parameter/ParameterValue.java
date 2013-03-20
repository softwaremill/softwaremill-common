package pl.softwaremill.common.cdi.autofactory.extension.parameter;

import javax.enterprise.inject.spi.BeanManager;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public interface ParameterValue {
    /**
     * Obtains a value of the argument that this class represents. Either uses the bean manager to obtain it,
     * or the provided array of arguments, that were passed to the factory method.
     */
    Object getValue(BeanManager bm, Object[] factoryParameters);
}
