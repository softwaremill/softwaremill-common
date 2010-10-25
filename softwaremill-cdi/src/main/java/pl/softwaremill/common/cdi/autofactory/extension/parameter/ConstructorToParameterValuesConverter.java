package pl.softwaremill.common.cdi.autofactory.extension.parameter;

import pl.softwaremill.common.cdi.autofactory.FactoryParameter;
import pl.softwaremill.common.cdi.autofactory.extension.FactoryMethodParameterIndexer;
import pl.softwaremill.common.cdi.autofactory.extension.QualifierAnnotationsFilter;

import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedParameter;
import java.util.List;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class ConstructorToParameterValuesConverter {
    private final QualifierAnnotationsFilter qualifierAnnotationsFilter;
    private final AnnotatedConstructor<?> constructor;
    private final FactoryMethodParameterIndexer factoryMethodParameterIndexer;

    public ConstructorToParameterValuesConverter(QualifierAnnotationsFilter qualifierAnnotationsFilter,
                                                 AnnotatedConstructor<?> constructor,
                                                 FactoryMethodParameterIndexer factoryMethodParameterIndexer) {
        this.qualifierAnnotationsFilter = qualifierAnnotationsFilter;
        this.constructor = constructor;
        this.factoryMethodParameterIndexer = factoryMethodParameterIndexer;
    }

    public ParameterValue[] convert() {
        List<? extends AnnotatedParameter<?>> constructorParameters = constructor.getParameters();
        ParameterValue[] constructorParameterValues = new ParameterValue[constructorParameters.size()];

        for (int i = 0; i < constructorParameters.size(); i++) {
            AnnotatedParameter<?> parameter = constructorParameters.get(i);
            constructorParameterValues[i] = createParameterValue(parameter);
        }

        return constructorParameterValues;
    }

    private ParameterValue createParameterValue(AnnotatedParameter parameter) {
        if (parameter.getAnnotation(FactoryParameter.class) != null) {
            return new FactoryParameterParameterValue(factoryMethodParameterIndexer.getIndexForArgument(parameter.getBaseType()));
        } else {
            return new BeanManagerParameterValue(parameter.getBaseType(), qualifierAnnotationsFilter.filter(parameter.getAnnotations()));
        }
    }
}
