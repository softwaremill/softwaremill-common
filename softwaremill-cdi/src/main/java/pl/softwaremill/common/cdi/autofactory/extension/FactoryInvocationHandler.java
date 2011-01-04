package pl.softwaremill.common.cdi.autofactory.extension;

import pl.softwaremill.common.cdi.autofactory.extension.parameter.ParameterValue;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.BeanManager;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class FactoryInvocationHandler<T> implements InvocationHandler {
    private final BeanManager beanManager;
    private final CreatedTypeData<T> createdTypeData;
    private final CreationalContext<T> creationalContext;

    public FactoryInvocationHandler(BeanManager beanManager, CreatedTypeData<T> createdTypeData,
                                    CreationalContext<T> creationalContext) {
        this.beanManager = beanManager;
        this.createdTypeData = createdTypeData;
        this.creationalContext = creationalContext;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] parameters) throws Throwable {
        T instance = createdTypeData.getCreatedTypeConstructor().newInstance(createConstructorParameters(parameters));
        if (!createdTypeData.isConstructorInjection()) {
            createdTypeData.getCreatedTypeInjectionTarget().inject(instance, creationalContext);
            createdTypeData.getCreatedTypeInjectionTarget().postConstruct(instance);
        }
        return instance;
    }

    private Object[] createConstructorParameters(Object[] factoryMethodParameters) {
        ParameterValue[] createdTypeConstructorParameterValues = createdTypeData.getCreatedTypeConstructorParameterValues();

        Object[] constructorParameters =  new Object[createdTypeConstructorParameterValues.length];

        for (int i = 0; i < createdTypeConstructorParameterValues.length; i++) {
            ParameterValue createdTypeConstructorParameterValue = createdTypeConstructorParameterValues[i];
            constructorParameters[i] = createdTypeConstructorParameterValue.getValue(beanManager, factoryMethodParameters);
        }

        return constructorParameters;
    }
}
