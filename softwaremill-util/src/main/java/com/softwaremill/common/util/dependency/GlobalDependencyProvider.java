package com.softwaremill.common.util.dependency;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class GlobalDependencyProvider extends AbstractDependencyProvider {
    private final List<Object> dependencies;
    private final List<QualifiedDependency> dependenciesWithQualifiers;

    public GlobalDependencyProvider(List<Object> deps) {
        dependencies = new ArrayList<Object>();
        dependenciesWithQualifiers = new ArrayList<QualifiedDependency>();

        loadDeps(deps);
    }

    @Override
    protected List<Object> getDependencies() {
        return dependencies;
    }

    @Override
    protected List<QualifiedDependency> getDependenciesWithQualifiers() {
        return dependenciesWithQualifiers;
    }
}
