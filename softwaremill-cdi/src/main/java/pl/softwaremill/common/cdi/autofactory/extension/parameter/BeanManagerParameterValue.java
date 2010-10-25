package pl.softwaremill.common.cdi.autofactory.extension.parameter;

import pl.softwaremill.common.cdi.util.BeanInject;

import javax.enterprise.inject.spi.BeanManager;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class BeanManagerParameterValue implements ParameterValue {
    private final Type beanType;
    private final Annotation[] qualifiers;

    public BeanManagerParameterValue(Type beanType, Annotation[] qualifiers) {
        this.beanType = beanType;
        this.qualifiers = qualifiers;
    }

    @Override
    public Object getValue(BeanManager bm, Object[] factoryParameters) {
        return BeanInject.lookup(bm, beanType, qualifiers);
    }
}
