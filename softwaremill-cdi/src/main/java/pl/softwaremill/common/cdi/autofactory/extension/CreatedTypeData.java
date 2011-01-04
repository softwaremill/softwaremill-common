package pl.softwaremill.common.cdi.autofactory.extension;

import pl.softwaremill.common.cdi.autofactory.extension.parameter.ParameterValue;

import javax.enterprise.inject.spi.InjectionTarget;
import java.lang.reflect.Constructor;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class CreatedTypeData<T> {
    private final ParameterValue[] createdTypeConstructorParameterValues;
    private final Constructor<T> createdTypeConstructor;
    private final InjectionTarget<T> createdTypeInjectionTarget;
    private final boolean constructorInjection;

    public CreatedTypeData(ParameterValue[] createdTypeConstructorParameterValues,
                           Constructor<T> createdTypeConstructor,
                           InjectionTarget<T> createdTypeInjectionTarget,
                           boolean constructorInjection) {
        this.createdTypeConstructorParameterValues = createdTypeConstructorParameterValues;
        this.createdTypeConstructor = createdTypeConstructor;
        this.createdTypeInjectionTarget = createdTypeInjectionTarget;
        this.constructorInjection = constructorInjection;
    }

    public ParameterValue[] getCreatedTypeConstructorParameterValues() {
        return createdTypeConstructorParameterValues;
    }

    public Constructor<T> getCreatedTypeConstructor() {
        return createdTypeConstructor;
    }

    public InjectionTarget<T> getCreatedTypeInjectionTarget() {
        return createdTypeInjectionTarget;
    }

    public boolean isConstructorInjection() {
        return constructorInjection;
    }
}
