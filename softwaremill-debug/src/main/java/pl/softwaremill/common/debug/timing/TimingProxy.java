package pl.softwaremill.common.debug.timing;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class TimingProxy {
    @SuppressWarnings({"unchecked"})
    public static <T, U extends T> T createFor(final U instance, Class<T> _interface) {
        return (T) Proxy.newProxyInstance(instance.getClass().getClassLoader(), new Class[]{ _interface },
                new InvocationHandler() {
                    @SuppressWarnings({"unchecked"})
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        long t0 = System.nanoTime();
                        try {
                            return method.invoke(instance, args);
                        } finally {
                            long dt = System.nanoTime() - t0;
                            TimingResults.instance.addInvocation(method, dt);
                        }
                    }
                });
    }
}
