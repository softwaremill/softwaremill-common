package pl.softwaremill.common.util.dependency;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Set;

/**
 * Holder for dependencies
 */
public class QualifiedDependency {
    private final Set<Annotation> qualifiers;
    private final Object dep;

    public Set<Annotation> getQualifiers() {
        return qualifiers;
    }

    public Object getDep() {
        return dep;
    }

    public QualifiedDependency(Annotation[] qualifiers, Object dep) {

        this.qualifiers = D.createKeyForAnnotations(qualifiers);

        this.qualifiers.addAll(Arrays.asList(qualifiers));
        this.dep = dep;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QualifiedDependency)) return false;

        QualifiedDependency that = (QualifiedDependency) o;

        if (dep != null ? !dep.equals(that.dep) : that.dep != null) return false;
        if (qualifiers != null ? !qualifiers.equals(that.qualifiers) : that.qualifiers != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = qualifiers != null ? qualifiers.hashCode() : 0;
        result = 31 * result + (dep != null ? dep.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "QualifiedDependency{" +
                "qualifiers=" + qualifiers +
                ", dep=" + dep +
                '}';
    }

    public boolean qualifiersEqual(Set<Annotation> qualifiers) {
        if (qualifiers == null) {
            if(this.qualifiers == null) {
                return true;
            }

            return false;
        }

        if (this.qualifiers.size() != qualifiers.size()) {
            return false;
        }

        for (Annotation qualifier : qualifiers) {
            boolean found = false;
            for (Annotation annotation : this.qualifiers) {
                if (qualifier.annotationType().equals(annotation.annotationType())) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                return false;
            }
        }

        return true;
    }
}
