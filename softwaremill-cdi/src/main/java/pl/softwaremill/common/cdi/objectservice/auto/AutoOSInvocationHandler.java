package pl.softwaremill.common.cdi.objectservice.auto;

import pl.softwaremill.common.util.dependency.BeanManagerDependencyProvider;

import javax.enterprise.inject.spi.BeanManager;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;

/**
 * Handler for the @OS invocation
 */
public class AutoOSInvocationHandler implements InvocationHandler {

    private AutoObjectServiceExtension autoObjectServiceExtension;
    private BeanManager beanManager;

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

                Class serviceClass = autoObjectServiceExtension.autoOSMap.get(method.getDeclaringClass())
                        .get(toService);
                Object o =  new BeanManagerDependencyProvider(beanManager).inject(
                        serviceClass, serviceClass.getAnnotation(OSImpl.class));

                return method.invoke(o, args);
            }
        }

        throw new RuntimeException("Couldn't find appropriate ObjectService for class "+toService);
    }
}
