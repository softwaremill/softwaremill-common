package pl.softwaremill.common.cdi.autofactory.extension;

import pl.softwaremill.common.cdi.autofactory.CreatedWith;
import pl.softwaremill.common.cdi.autofactory.extension.parameter.ConstructorToParameterValuesConverter;
import pl.softwaremill.common.cdi.autofactory.extension.parameter.ParameterValue;

import javax.enterprise.inject.spi.*;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class AutoFactoryFromCreatedWithCreator {
    private final BeanManager beanManager;
    private final AnnotatedType<?> createdType;
    private final Class<?> factoryClass;

    public AutoFactoryFromCreatedWithCreator(BeanManager beanManager, CreatedWith createdWithAnnotation,
                                             ProcessAnnotatedType<?> event) {
        this.beanManager = beanManager;
        this.createdType = event.getAnnotatedType();
        this.factoryClass = createdWithAnnotation.value();
    }

    public Bean create() {
        checkFactoryClassIsAnInterface();

        Method soleFactoryMethod = getSoleFactoryMethod();
        AnnotatedConstructor<?> soleCreatedTypeConstructor = getSoleConstructorOfCreatedType();

        MethodParameterIndexer methodParameterIndexer = new MethodParameterIndexer(soleFactoryMethod);
        ParameterValue[] createdTypeConstructorParameterValues = new ConstructorToParameterValuesConverter(
                new QualifierAnnotationsFilter(beanManager), soleCreatedTypeConstructor, methodParameterIndexer).convert();

        return new AutoFactoryBean(beanManager, factoryClass, createdTypeConstructorParameterValues,
                soleCreatedTypeConstructor.getJavaMember());
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

    private AnnotatedConstructor<?> getSoleConstructorOfCreatedType() {
        Set<? extends AnnotatedConstructor<?>> constructors = createdType.getConstructors();
        if (constructors.size() != 1) {
            throw new RuntimeException("A bean created with an auto-factory can have only one constructor: " +
                    createdType);
        }

        return constructors.iterator().next();
    }
}
