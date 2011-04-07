package pl.softwaremill.common.util.dependency;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class GlobalDependencyProvider implements DependencyProvider {
    private final List<Object> dependencies;

    public GlobalDependencyProvider(List<Object> deps) {
        dependencies = deps;
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

    @Override
    public String toString() {
        return "GlobalDependencyProvider{dependencies=" + dependencies + "}";
    }
}
