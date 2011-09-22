package pl.softwaremill.common.util.dependency;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *  Common class for dependency providers
 */
public abstract class AbstractDependencyProvider implements DependencyProvider{

    public AbstractDependencyProvider() {
    }

    protected void loadDeps(List<Object> deps) {
        for (Object dep : deps) {
            if (dep instanceof QualifiedDependency) {
                getDependenciesWithQualifiers().add((QualifiedDependency) dep);
            } else {
                getDependencies().add(dep);
            }
        }
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public <T> T inject(Class<T> cls, Annotation... qualifiers) {
        // Only dependencies without qualifiers are supported by this provider
        if (qualifiers.length > 0) {
            // check for dependencies with qualifiers
            Set<Annotation> key = D.createKeyForAnnotations(qualifiers);
            for (QualifiedDependency qualifiedDependency : getDependenciesWithQualifiers()) {
                if (qualifiedDependency.qualifiersEqual(key)) {
                    // depednecy has to be right class
                    if (cls.isAssignableFrom(qualifiedDependency.getDep().getClass())) {
                        return (T) qualifiedDependency.getDep();
                    }
                }
            }

            // none found
            return null;
        }

        for (Object dependency : getDependencies()) {
            if (cls.isAssignableFrom(dependency.getClass())) {
                return (T) dependency;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()+"{dependencies=" + getDependencies() +
                ";qualifiefDependences=" + getDependenciesWithQualifiers() + "}";
    }

    protected abstract List<Object> getDependencies();
    protected abstract List<QualifiedDependency> getDependenciesWithQualifiers();
}
