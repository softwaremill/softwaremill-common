package pl.softwaremill.common.util.dependency;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class ThreadLocalDependencyProvider extends AbstractDependencyProvider {

    private final ThreadLocal<List<Object>> dependencies;
    private final ThreadLocal<List<QualifiedDependency>> dependenciesWithQualifiers;

    public ThreadLocalDependencyProvider(List<Object> deps) {
        dependenciesWithQualifiers = new ThreadLocal<List<QualifiedDependency>>() {
            @Override
            protected List<QualifiedDependency> initialValue() {
                return new ArrayList<QualifiedDependency>();
            }
        };

        dependencies = new ThreadLocal<List<Object>>() {
            @Override
            protected List<Object> initialValue() {
                return new ArrayList<Object>();
            }
        };

        loadDeps(deps);
    }

    @Override
    protected List<Object> getDependencies() {
        return dependencies.get();
    }

    @Override
    protected List<QualifiedDependency> getDependenciesWithQualifiers() {
        return dependenciesWithQualifiers.get();
    }
}
