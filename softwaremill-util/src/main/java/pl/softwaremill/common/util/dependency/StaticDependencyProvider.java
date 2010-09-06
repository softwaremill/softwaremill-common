package pl.softwaremill.common.util.dependency;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class StaticDependencyProvider implements DependencyProvider {
    private final Map<DependencyKey, Object> dependenciesByClass;

    public StaticDependencyProvider() {
        dependenciesByClass = new ConcurrentHashMap<DependencyKey, Object>();
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public <T> T inject(Class<T> cls, Annotation... qualifiers) {
        return (T) dependenciesByClass.get(new DependencyKey(cls, qualifiers));
    }

    public <T> void register(T dependency, Class<T> cls, Annotation... qualifiers) {
        dependenciesByClass.put(new DependencyKey(cls, qualifiers), dependency);
    }

    public void unregister(Class<?> cls, Annotation... qualifiers) {
        dependenciesByClass.remove(new DependencyKey(cls, qualifiers));
    }

    private static class DependencyKey {
        private final Class<?> cls;
        private final Annotation[] qualififers;

        private DependencyKey(Class<?> cls, Annotation[] qualififers) {
            this.cls = cls;
            this.qualififers = qualififers;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof DependencyKey)) return false;

            DependencyKey that = (DependencyKey) o;

            if (cls != null ? !cls.equals(that.cls) : that.cls != null) return false;
            if (!Arrays.equals(qualififers, that.qualififers)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = cls != null ? cls.hashCode() : 0;
            result = 31 * result + (qualififers != null ? Arrays.hashCode(qualififers) : 0);
            return result;
        }
    }

    @Override
    public String toString() {
        return "StaticDependencyProvider{dependenciesByClass=" + dependenciesByClass + "}";
    }
}
