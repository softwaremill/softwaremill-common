package pl.softwaremill.common.util.dependency;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class ThreadLocalDependencyProvider implements DependencyProvider {
    private final ThreadLocal<List<Object>> dependencies;

    public ThreadLocalDependencyProvider() {
        dependencies = new ThreadLocal<List<Object>>() {
            @Override
            protected List<Object> initialValue() {
                return new ArrayList<Object>();
            }
        };
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

    public void register(Object dependency) {
        dependencies.get().add(dependency);
    }

    public void unregister(Object dependency) {
        dependencies.get().remove(dependency);
    }

    @Override
    public String toString() {
        return "ThreadLocalDependencyProvider{dependencies=" + dependencies.get() + "}";
    }
}
