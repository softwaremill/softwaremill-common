package pl.softwaremill.common.cdi.autofactory.extension.parameter;

import org.jboss.weld.Container;
import org.jboss.weld.injection.ConstructorInjectionPoint;
import org.jboss.weld.injection.CurrentInjectionPoint;
import org.jboss.weld.injection.ParameterInjectionPoint;
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
        CurrentInjectionPoint currentInjectionPointStack = Container.instance().services().get(CurrentInjectionPoint.class);
        ConstructorInjectionPoint currentInjectionPoint = (ConstructorInjectionPoint) currentInjectionPointStack.peek();
        currentInjectionPointStack.push(findParameterInjectionPoint(currentInjectionPoint));

        Object result = BeanInject.lookup(bm, beanType, qualifiers);

        currentInjectionPointStack.pop();

        return result;
    }

    private ParameterInjectionPoint findParameterInjectionPoint(ConstructorInjectionPoint constructorInjectionPoint) {
        for (Object parameterInjectionPointObj : constructorInjectionPoint.getWeldParameters()) {
            ParameterInjectionPoint parameterInjectionPoint = (ParameterInjectionPoint) parameterInjectionPointObj;
            if (beanType.equals(parameterInjectionPoint.getBaseType())) {
                return parameterInjectionPoint;
            }
        }

        throw new RuntimeException("Cannot find a parameter injection point for " + beanType + " in constructor " +
                constructorInjectionPoint);
    }
}
