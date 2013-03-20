package com.softwaremill.common.cdi.objectservice.auto;

import com.softwaremill.common.cdi.objectservice.extension.ObjectServiceSpecification;
import com.softwaremill.common.util.dependency.BeanManagerDependencyProvider;

import javax.enterprise.inject.spi.BeanManager;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;

/**
 * Handler for the @OS invocation
 */
public class AutoOSInvocationHandler implements InvocationHandler {

    private AutoObjectServiceExtension autoObjectServiceExtension;
    private BeanManager beanManager;

    /**
     * Cache for already resolved beans
     */
    private Map<Class, Object> beanCache = new HashMap<Class, Object>();

    public AutoOSInvocationHandler(AutoObjectServiceExtension autoObjectServiceExtension, BeanManager beanManager) {
        this.autoObjectServiceExtension = autoObjectServiceExtension;
        this.beanManager = beanManager;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class toService = null;

        for (int i = 0; i < method.getGenericParameterTypes().length; i++) {
            if (method.getGenericParameterTypes()[i] instanceof TypeVariable) {
                // found it, invoke
                toService = args[i].getClass();

                Object bean = null;

                // first check the cache
                if ((bean = beanCache.get(toService)) != null) {

                } else {
                    Class<?> interfaceClass = method.getDeclaringClass();

                    Class serviceClass = autoObjectServiceExtension.autoOSMap.get(interfaceClass)
                            .get(toService);

                    if (serviceClass == null) {
                        // try with inheritance if there's no direct match

                        serviceClass = serviceForObject(toService, interfaceClass);
                    }
                    try {
                        beanCache.put(toService, bean = new BeanManagerDependencyProvider(beanManager).inject(
                                serviceClass, serviceClass.getAnnotation(OSImpl.class)));
                    } catch (Exception e) {
                        throw new AutoOSException("Cannot resolve implementation of @OS " + interfaceClass
                                + " for object of type " + toService, e);
                    }
                }

                if (bean != null) {
                    return method.invoke(bean, args);
                }
            }
        }

        throw new AutoOSException("Couldn't find appropriate ObjectServiceImpl for class " + toService);
    }

    private Class serviceForObject(Class<?> objClass, Class<?> serviceClass) {
        // TODO: cache results

        Class bestSoFar = null;

        // Checking all registered specifications
        for (Class toService : autoObjectServiceExtension.autoOSMap.get(serviceClass).keySet()) {
            // Checking if the service is suitable for the requested one
            // Here we allow subclasses of objClass, to make sure that e.g. Hibernate Proxies work. However, this
            // will not work if there are object services for non-terminal nodes (non-leaves) in the inheritance tree.
            if (toService.isAssignableFrom(objClass)) {
                // Checking if it's better (more specific) than the one currently found
                if (bestSoFar == null || bestSoFar.isAssignableFrom(toService)) {
                    bestSoFar = toService;
                }

                // TODO: check if there's no ambiguency (two "best")
            }
        }

        return autoObjectServiceExtension.autoOSMap.get(serviceClass).get(bestSoFar);
    }
}
