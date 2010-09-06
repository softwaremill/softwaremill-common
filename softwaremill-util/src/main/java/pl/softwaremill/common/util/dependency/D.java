package pl.softwaremill.common.util.dependency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class D {
    private final static Logger log = LoggerFactory.getLogger(D.class);

    private static List<DependencyProvider> providers = new ArrayList<DependencyProvider>();

    /**
     * Try to find the given dependency in the registered providers.
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
     * @param provider A new dependency provider to add. The provider will be added to the end of the providers list.
     */
    public static void register(DependencyProvider provider) {
        log.info("Registering " + provider);
        providers.add(provider);
    }

    public static void unregister(DependencyProvider provider) {
        log.info("Unregistering " + provider);
        providers.remove(provider);
    }
}
