package pl.softwaremill.common.cdi.autofactory.extension;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import javax.enterprise.inject.spi.BeanManager;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Set;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class QualifierAnnotationsFilter {
    private final BeanManager beanManager;

    public QualifierAnnotationsFilter(BeanManager beanManager) {
        this.beanManager = beanManager;
    }

    public Annotation[] filter(Set<Annotation> annotations) {
        Collection<Annotation> filtered = Collections2.filter(annotations, new Predicate<Annotation>() {
            @Override
            public boolean apply(Annotation input) {
                return beanManager.isQualifier(input.annotationType());
            }
        });

        return filtered.toArray(new Annotation[filtered.size()]);
    }
}
