package com.softwaremill.common.cdi.autofactory.extension.parameter.converter;

import com.softwaremill.common.cdi.autofactory.extension.MethodParameterIndexer;
import com.softwaremill.common.cdi.autofactory.extension.parameter.FactoryParameterParameterValue;
import com.softwaremill.common.cdi.autofactory.extension.parameter.ParameterValue;

import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedParameter;
import java.util.List;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class FactoryParameterOnlyConstructorConverter implements ConstructorToParameterValuesConverter {
    private final AnnotatedConstructor<?> constructor;
    private final MethodParameterIndexer methodParameterIndexer;

    public FactoryParameterOnlyConstructorConverter(AnnotatedConstructor<?> constructor,
                                                    MethodParameterIndexer methodParameterIndexer) {
        this.constructor = constructor;
        this.methodParameterIndexer = methodParameterIndexer;
    }

    public ParameterValue[] convert() {
        List<? extends AnnotatedParameter<?>> constructorParameters = constructor.getParameters();
        ParameterValue[] constructorParameterValues = new ParameterValue[constructorParameters.size()];

        for (int i = 0; i < constructorParameters.size(); i++) {
            AnnotatedParameter<?> parameter = constructorParameters.get(i);
            constructorParameterValues[i] = new FactoryParameterParameterValue(
                    methodParameterIndexer.getIndexForArgument(parameter.getBaseType()));
        }

        return constructorParameterValues;
    }
}
