package com.softwaremill.common.util.dependency;

import java.lang.annotation.Annotation;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public interface DependencyProvider {
    /**
     * @param cls Class of the dependency.
     * @param qualifiers Qualifiers of the dependency.
     * @return Dependency with the given class and qualifiers or {@code null} if no dependency is found.
     */
    <T> T inject(Class<T> cls, Annotation... qualifiers);
}
