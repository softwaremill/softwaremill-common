package com.softwaremill.common.cdi.objectservice.auto;

import org.jboss.weld.literal.DefaultLiteral;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;
import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;

/**
 * Bean that injects proxy for auto object services
 */
public class AutoOSBean<T> implements Bean<T> {
    private final BeanManager beanManager;
    private final Class<?> osClass;
    private AutoObjectServiceExtension autoObjectServiceExtension;

    private final Class<?>[] autoOSClassInArray;

    public AutoOSBean(BeanManager beanManager, Class<?> osClass, AutoObjectServiceExtension autoObjectServiceExtension) {
        this.beanManager = beanManager;
        this.osClass = osClass;
        this.autoObjectServiceExtension = autoObjectServiceExtension;

        this.autoOSClassInArray = new Class<?>[] { this.osClass};
    }

    @Override
    public Set<Type> getTypes() {
        return Collections.<Type>singleton(osClass);
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public T create(CreationalContext<T> creationalContext) {
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                autoOSClassInArray,
                new AutoOSInvocationHandler(autoObjectServiceExtension, beanManager));
    }

    @Override
    public void destroy(Object instance, CreationalContext creationalContext) { }

    @Override
    public Set<Annotation> getQualifiers() {
        return Collections.<Annotation>singleton(DefaultLiteral.INSTANCE);
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return Dependent.class;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Set<Class<? extends Annotation>> getStereotypes() {
        return Collections.emptySet();
    }

    @Override
    public Class<?> getBeanClass() {
        return osClass;
    }

    @Override
    public boolean isAlternative() {
        return false;
    }

    @Override
    public boolean isNullable() {
        return false;
    }

    @Override
    public Set<InjectionPoint> getInjectionPoints() {
        return Collections.emptySet();
    }
}
