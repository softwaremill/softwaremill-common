package pl.softwaremill.common.cdi.autofactory.extension.parameter.converter;

import pl.softwaremill.common.cdi.autofactory.FactoryParameter;
import pl.softwaremill.common.cdi.autofactory.extension.MethodParameterIndexer;
import pl.softwaremill.common.cdi.autofactory.extension.QualifierAnnotationsFilter;
import pl.softwaremill.common.cdi.autofactory.extension.parameter.BeanManagerParameterValue;
import pl.softwaremill.common.cdi.autofactory.extension.parameter.FactoryParameterParameterValue;
import pl.softwaremill.common.cdi.autofactory.extension.parameter.ParameterValue;

import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedParameter;
import java.util.List;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class MixedConstructorConverter implements ConstructorToParameterValuesConverter {
    private final QualifierAnnotationsFilter qualifierAnnotationsFilter;
    private final AnnotatedConstructor<?> constructor;
    private final MethodParameterIndexer methodParameterIndexer;

    public MixedConstructorConverter(QualifierAnnotationsFilter qualifierAnnotationsFilter,
                                                 AnnotatedConstructor<?> constructor,
                                                 MethodParameterIndexer methodParameterIndexer) {
        this.qualifierAnnotationsFilter = qualifierAnnotationsFilter;
        this.constructor = constructor;
        this.methodParameterIndexer = methodParameterIndexer;
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
            return new FactoryParameterParameterValue(methodParameterIndexer.getIndexForArgument(parameter.getBaseType()));
        } else {
            return new BeanManagerParameterValue(parameter.getBaseType(), qualifierAnnotationsFilter.filter(parameter.getAnnotations()));
        }
    }
}
