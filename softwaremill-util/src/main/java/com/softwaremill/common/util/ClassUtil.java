package com.softwaremill.common.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class ClassUtil {
    /**
     * @param names Class names to check.
     * @return Name of the class that is present in the classpath, one of {@code names}.
     * @throws RuntimeException If none of the given classes is found.
     */
    public static String findClassPresentInClasspath(String... names) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        for (String name : names) {
            try {
                cl.loadClass(name);
                return name;
            } catch (ClassNotFoundException ignore) {
                // Empty
            }
        }

        throw new RuntimeException("None of the given classes where found on the classpath: " + Arrays.toString(names));
    }

    public static <T> T newInstance(String className, Class<T> expectedType) {
        try {
            return expectedType.cast(Thread.currentThread().getContextClassLoader().loadClass(className).newInstance());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Annotation instantiateAnnotation(final Class<? extends Annotation> annotationClass) {
        // source http://stackoverflow.com/questions/2786292/is-it-possible-to-instantiate-a-java-annotation-given-a-class-extends-annotatio

        return (Annotation) Proxy.newProxyInstance(
                annotationClass.getClassLoader(),
                new Class[]{Annotation.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        if (method.getName().equals("hashCode")) {
                            return annotationClass.getName().hashCode();
                        }
                        else if (method.getName().equals("toString")) {
                            return "[AnnotationObj:"+annotationClass.getName()+"]";
                        }
                        else if (method.getName().equals("equals")) {
                            if (args[0] == null) {
                                return false;
                            }

                            Annotation annotation = (Annotation)args[0];

                            return annotationClass.getName().equals(annotation.annotationType().getName());
                        }

                        return annotationClass;
                    }
                });
    }
}
