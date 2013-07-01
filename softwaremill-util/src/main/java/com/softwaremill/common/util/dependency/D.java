package com.softwaremill.common.util.dependency;

import com.google.common.collect.ImmutableList;
import com.softwaremill.common.util.ClassUtil;
import org.jboss.weld.context.bound.BoundRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class D {
    private final static Logger log = LoggerFactory.getLogger(D.class);

    private static Deque<DependencyProvider> providers = new LinkedBlockingDeque<DependencyProvider>();

    /**
     * Try to find the given dependency in the registered providers.
     *
     * @throws RuntimeException If the dependency is not found in any provider.
     */
    public static <T> T inject(Class<T> cls, Annotation... qualifiers) {
        for (DependencyProvider provider : providers) {
            T result = provider.inject(cls, qualifiers);
            if (result != null) {
                return result;
            }
        }

        throw new RuntimeException("No dependencies of class " + cls + " (" + Arrays.toString(qualifiers) + ") found using providers: " + providers);
    }

    /**
     * @param provider A new dependency provider to add. The provider will be checked first when looking for
     *                 dependencies.
     */
    public static void register(DependencyProvider provider) {
        log.debug("Registering " + provider);
        providers.addFirst(provider);
    }

    public static void unregister(DependencyProvider provider) {
        log.debug("Unregistering " + provider);
        providers.remove(provider);
    }

    public static <T> T withDependencies(Object dep1, Callable<T> what) throws Exception {
        return withDependencies(ImmutableList.of(dep1), what);
    }

    public static <T> T withDependencies(Object dep1, Object dep2,
                                         Callable<T> what) throws Exception {
        return withDependencies(ImmutableList.of(dep1, dep2), what);
    }

    public static <T> T withDependencies(Object dep1, Object dep2, Object dep3,
                                         Callable<T> what) throws Exception {
        return withDependencies(ImmutableList.of(dep1, dep2, dep3), what);
    }

    public static <T> T withDependencies(List<Object> deps, Callable<T> what) throws Exception {
        ThreadLocalDependencyProvider dependencyProvider = new ThreadLocalDependencyProvider(deps);
        register(dependencyProvider);

        try {
            return what.call();
        } finally {
            unregister(dependencyProvider);
        }
    }

    public static Annotation qualifier(Class<? extends Annotation> annotationClass) {
        return ClassUtil.instantiateAnnotation(annotationClass);
    }

    public static QualifiedDependency qualifiedDependency(Object dep, Annotation... qualifiers) {
        return new QualifiedDependency(qualifiers, dep);
    }

    static Set<Annotation> createKeyForAnnotations(Annotation[] annotations) {
        return new HashSet<Annotation>(Arrays.asList(annotations));
    }

    public static void inRequestContext(final Runnable function) {
        inRequestContext(new HashMap<String, Object>(), function);
    }

    public static void inRequestContext(final Map<String, Object> context, final Runnable function) {
        final BoundRequestContext requestContext = D.inject(BoundRequestContext.class);
        try {
            requestContext.associate(context);
            requestContext.activate();
            function.run();
        } finally {
            requestContext.invalidate();
            requestContext.deactivate();
            requestContext.dissociate(context);
        }
    }

}
