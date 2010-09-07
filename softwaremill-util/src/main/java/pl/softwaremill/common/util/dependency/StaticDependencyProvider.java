package pl.softwaremill.common.util.dependency;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class StaticDependencyProvider implements DependencyProvider {
    private final List<Object> dependencies;

    public StaticDependencyProvider() {
        dependencies = new ArrayList<Object>();
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public <T> T inject(Class<T> cls, Annotation... qualifiers) {
        // Only dependencies without qualifiers are supported by this provider
        if (qualifiers.length > 0) {
            return null;
        }

        for (Object dependency : dependencies) {
            if (cls.isAssignableFrom(dependency.getClass())) {
                return (T) dependency;
            }
        }

        return null;
    }

    public void register(Object dependency) {
        dependencies.add(dependency);
    }

    public void unregister(Object dependency) {
        dependencies.remove(dependency);
    }

    @Override
    public String toString() {
        return "StaticDependencyProvider{dependencies=" + dependencies + "}";
    }
}
