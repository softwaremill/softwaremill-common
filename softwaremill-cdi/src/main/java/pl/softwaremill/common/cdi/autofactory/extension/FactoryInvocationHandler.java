package pl.softwaremill.common.cdi.autofactory.extension;

import pl.softwaremill.common.cdi.autofactory.extension.parameter.ParameterValue;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class FactoryInvocationHandler<T> implements InvocationHandler {
    private final BeanManager beanManager;
    private final Constructor<T> createdTypeConstructor;
    private final ParameterValue[] createdTypeConstructorParameterValues;
    private final InjectionTarget<T> createdTypeInjectionTarget;
    private final CreationalContext<T> creationalContext;
    private final boolean constructorInjection;

    public FactoryInvocationHandler(BeanManager beanManager, Constructor<T> createdTypeConstructor,
                                    ParameterValue[] createdTypeConstructorParameterValues,
                                    InjectionTarget<T> createdTypeInjectionTarget,
                                    CreationalContext<T> creationalContext,
                                    boolean constructorInjection) {
        this.beanManager = beanManager;
        this.createdTypeConstructor = createdTypeConstructor;
        this.createdTypeConstructorParameterValues = createdTypeConstructorParameterValues;
        this.createdTypeInjectionTarget = createdTypeInjectionTarget;
        this.creationalContext = creationalContext;
        this.constructorInjection = constructorInjection;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] parameters) throws Throwable {
        T instance = createdTypeConstructor.newInstance(createConstructorParameters(parameters));
        if (!constructorInjection) {
            createdTypeInjectionTarget.inject(instance, creationalContext);
            createdTypeInjectionTarget.postConstruct(instance);
        }
        return instance;
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
