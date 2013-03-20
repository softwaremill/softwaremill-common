package com.softwaremill.common.cdi.autofactory.extension;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.jboss.weld.manager.BeanManagerImpl;
import org.jboss.weld.manager.SimpleInjectionTarget;
import org.jboss.weld.resources.ClassTransformer;
import com.softwaremill.common.cdi.autofactory.CreatedWith;
import com.softwaremill.common.cdi.autofactory.extension.parameter.ParameterValue;
import com.softwaremill.common.cdi.autofactory.extension.parameter.converter.ConstructorToParameterValuesConverter;
import com.softwaremill.common.cdi.autofactory.extension.parameter.converter.FactoryParameterOnlyConstructorConverter;
import com.softwaremill.common.cdi.autofactory.extension.parameter.converter.MixedConstructorConverter;

import javax.annotation.Nullable;
import javax.enterprise.inject.spi.*;
import javax.inject.Inject;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Set;

import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Iterables.concat;

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
        AnnotatedConstructor<T> soleCreatedTypeConstructor = getSoleAcceptableConstructorOfCreatedType();

        boolean constructorInjection = isConstructorInjection(soleCreatedTypeConstructor);

        MethodParameterIndexer methodParameterIndexer = new MethodParameterIndexer(soleFactoryMethod);
        ConstructorToParameterValuesConverter converter = getConverter(soleCreatedTypeConstructor,
                methodParameterIndexer, constructorInjection);
        ParameterValue[] createdTypeConstructorParameterValues = converter.convert();

        // We cannot use BeanManager.createInjectionTarget as this adds the injection target to validation
        InjectionTarget<T> injectionTarget = createInjectionTargetWithoutValidation();

        return new AutoFactoryBean<T>(beanManager, factoryClass,
                new CreatedTypeData<T>(createdTypeConstructorParameterValues,
                soleCreatedTypeConstructor, injectionTarget,
                constructorInjection));
    }

    private SimpleInjectionTarget<T> createInjectionTargetWithoutValidation() {
        BeanManagerImpl bmi = (BeanManagerImpl) beanManager;
        return new SimpleInjectionTarget<T>(bmi.getServices().get(ClassTransformer.class).loadClass(createdType), bmi);
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

    private AnnotatedConstructor<T> getSoleAcceptableConstructorOfCreatedType() {
        /* Yup, I know, imperatively it would've been shorter */
        final Predicate<AnnotatedConstructor<T>> diConstructorPredicate = new Predicate<AnnotatedConstructor<T>>() {
            @Override
            public boolean apply(@Nullable AnnotatedConstructor<T> input) {
                return input.getJavaMember().getAnnotation(Inject.class) != null;
            }
        };

        Set<? extends AnnotatedConstructor<T>> allConstructors = createdType.getConstructors();
        Collection<? extends AnnotatedConstructor<T>> diConstructors = filter(allConstructors,
                diConstructorPredicate);

        if (diConstructors.size() > 1 || (allConstructors.size() > 1 && diConstructors.size() == 0)) {
            throw new RuntimeException("A bean created with an auto-factory " +
                    "can have only one constructor: " +
                    createdType);
        }

        return Iterables.get(concat(diConstructors, allConstructors), 0);
    }

    private ConstructorToParameterValuesConverter getConverter(AnnotatedConstructor<?> soleCreatedTypeConstructor,
                                                               MethodParameterIndexer methodParameterIndexer,
                                                               boolean constructorInjection) {
        if (constructorInjection) {
            return new MixedConstructorConverter(new QualifierAnnotationsFilter(beanManager),
                    soleCreatedTypeConstructor, methodParameterIndexer);
        } else {
            // If the constructor isn't annotated with @Inject, the no dependencies are injected, all parameters
            // are taken from the factory method.
            return new FactoryParameterOnlyConstructorConverter(soleCreatedTypeConstructor, methodParameterIndexer);
        }
    }

    private boolean isConstructorInjection(AnnotatedConstructor<T> soleCreatedTypeConstructor) {
        return soleCreatedTypeConstructor.getAnnotation(Inject.class) != null;
    }
}
