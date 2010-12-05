package pl.softwaremill.common.cdi.autofactory.extension;

import pl.softwaremill.common.cdi.autofactory.CreatedWith;
import pl.softwaremill.common.cdi.autofactory.extension.parameter.ParameterValue;
import pl.softwaremill.common.cdi.autofactory.extension.parameter.converter.ConstructorToParameterValuesConverter;
import pl.softwaremill.common.cdi.autofactory.extension.parameter.converter.FactoryParameterOnlyConstructorConverter;
import pl.softwaremill.common.cdi.autofactory.extension.parameter.converter.MixedConstructorConverter;

import javax.enterprise.inject.spi.*;
import javax.inject.Inject;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class AutoFactoryFromCreatedWithCreator<T> {
    private final BeanManager beanManager;
    private final AnnotatedType<T> createdType;
    private final Class<?> factoryClass;

    public AutoFactoryFromCreatedWithCreator(BeanManager beanManager, CreatedWith createdWithAnnotation,
                                             ProcessAnnotatedType<T> event) {
        this.beanManager = beanManager;
        this.createdType = event.getAnnotatedType();
        this.factoryClass = createdWithAnnotation.value();
    }

    public Bean<T> create() {
        checkFactoryClassIsAnInterface();

        Method soleFactoryMethod = getSoleFactoryMethod();
        AnnotatedConstructor<T> soleCreatedTypeConstructor = getSoleConstructorOfCreatedType();

        MethodParameterIndexer methodParameterIndexer = new MethodParameterIndexer(soleFactoryMethod);
        ConstructorToParameterValuesConverter converter = getConverter(soleCreatedTypeConstructor, methodParameterIndexer);
        ParameterValue[] createdTypeConstructorParameterValues = converter.convert();

        return new AutoFactoryBean<T>(beanManager, factoryClass, createdTypeConstructorParameterValues,
                soleCreatedTypeConstructor.getJavaMember(), beanManager.createInjectionTarget(createdType));
    }

    private void checkFactoryClassIsAnInterface() {
        if (!factoryClass.isInterface()) {
            throw new RuntimeException("A factory class "  + factoryClass + "specified in the argument of @CreatedWith for " +
                    createdType + " is not an interface. ");
        }
    }

    private Method getSoleFactoryMethod() {
        if (factoryClass.getMethods().length != 1) {
            throw new RuntimeException("A factory class (" + factoryClass + ") can have only one method. " +
                    "Used through @CreatedWith by: " + createdType);
        }
        
        return factoryClass.getMethods()[0];
    }

    private AnnotatedConstructor<T> getSoleConstructorOfCreatedType() {
        Set<? extends AnnotatedConstructor<T>> constructors = createdType.getConstructors();
        if (constructors.size() != 1) {
            throw new RuntimeException("A bean created with an auto-factory can have only one constructor: " +
                    createdType);
        }

        return constructors.iterator().next();
    }

    private ConstructorToParameterValuesConverter getConverter(AnnotatedConstructor<?> soleCreatedTypeConstructor,
                                                               MethodParameterIndexer methodParameterIndexer) {
        if (soleCreatedTypeConstructor.getAnnotation(Inject.class) != null) {
            return new MixedConstructorConverter(new QualifierAnnotationsFilter(beanManager),
                    soleCreatedTypeConstructor, methodParameterIndexer);
        } else {
            // If the constructor isn't annotated with @Inject, the no dependencies are injected, all parameters
            // are taken from the factory method.
            return new FactoryParameterOnlyConstructorConverter(soleCreatedTypeConstructor, methodParameterIndexer);
        }
    }
}
