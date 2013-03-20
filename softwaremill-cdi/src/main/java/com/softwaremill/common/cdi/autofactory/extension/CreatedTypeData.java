package com.softwaremill.common.cdi.autofactory.extension;

import com.softwaremill.common.cdi.autofactory.extension.parameter.ParameterValue;

import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.InjectionTarget;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class CreatedTypeData<T> {
    private final ParameterValue[] createdTypeConstructorParameterValues;
    private final AnnotatedConstructor<T> createdTypeConstructor;
    private final InjectionTarget<T> createdTypeInjectionTarget;
    private final boolean constructorInjection;

    public CreatedTypeData(ParameterValue[] createdTypeConstructorParameterValues,
                           AnnotatedConstructor<T> createdTypeConstructor,
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

    public AnnotatedConstructor<T> getCreatedTypeConstructor() {
        return createdTypeConstructor;
    }

    public InjectionTarget<T> getCreatedTypeInjectionTarget() {
        return createdTypeInjectionTarget;
    }

    public boolean isConstructorInjection() {
        return constructorInjection;
    }
}
