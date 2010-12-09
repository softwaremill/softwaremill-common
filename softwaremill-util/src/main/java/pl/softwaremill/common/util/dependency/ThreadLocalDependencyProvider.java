package pl.softwaremill.common.util.dependency;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class ThreadLocalDependencyProvider implements DependencyProvider {
    private final ThreadLocal<List<Object>> dependencies;

    public ThreadLocalDependencyProvider(List<Object> deps) {
        dependencies = new ThreadLocal<List<Object>>() {
            @Override
            protected List<Object> initialValue() {
                return new ArrayList<Object>();
            }
        };

        dependencies.set(deps);
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public <T> T inject(Class<T> cls, Annotation... qualifiers) {
        // Only dependencies without qualifiers are supported by this provider
        if (qualifiers.length > 0) {
            return null;
        }

        for (Object dependency : dependencies.get()) {
            if (cls.isAssignableFrom(dependency.getClass())) {
                return (T) dependency;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return "ThreadLocalDependencyProvider{dependencies=" + dependencies.get() + "}";
    }
}
