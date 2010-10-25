package pl.softwaremill.common.cdi.autofactory.extension;

import pl.softwaremill.common.cdi.autofactory.extension.parameter.ParameterValue;

import javax.enterprise.inject.spi.BeanManager;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class FactoryInvocationHandler implements InvocationHandler {
    private final BeanManager beanManager;
    private final Constructor<?> createdTypeConstructor;
    private final ParameterValue[] createdTypeConstructorParameterValues;

    public FactoryInvocationHandler(BeanManager beanManager, Constructor<?> createdTypeConstructor,
                                    ParameterValue[] createdTypeConstructorParameterValues) {
        this.beanManager = beanManager;
        this.createdTypeConstructor = createdTypeConstructor;
        this.createdTypeConstructorParameterValues = createdTypeConstructorParameterValues;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] parameters) throws Throwable {
        return createdTypeConstructor.newInstance(createConstructorParameters(parameters));
    }

    private Object[] createConstructorParameters(Object[] factoryMethodParameters) {
        Object[] constructorParameters =  new Object[createdTypeConstructorParameterValues.length];

        for (int i = 0; i < createdTypeConstructorParameterValues.length; i++) {
            ParameterValue createdTypeConstructorParameterValue = createdTypeConstructorParameterValues[i];
            constructorParameters[i] = createdTypeConstructorParameterValue.getValue(beanManager, factoryMethodParameters);
        }

        return constructorParameters;
    }
}
