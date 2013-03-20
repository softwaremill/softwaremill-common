package pl.softwaremill.common.util.dependency;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Set;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class BeanManagerDependencyProvider implements DependencyProvider {
    private final BeanManager beanManager;

    public BeanManagerDependencyProvider(BeanManager beanManager) {
        this.beanManager = beanManager;
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public <T> T inject(Class<T> cls, Annotation... qualifiers) {
        Set<?> beans = beanManager.getBeans(cls, qualifiers);
        if (beans.size() != 1) {
            if (beans.size() == 0) {
                return null;
            } else {
                throw new RuntimeException("Multiple beans of class " + cls + " (" + Arrays.toString(qualifiers) + " found: " + beans + ".");
            }
        }

        Bean<T> myBean = (Bean<T>) beans.iterator().next();
        return (T) beanManager.getReference(myBean, cls, beanManager.createCreationalContext(myBean));
    }

    @Override
    public String toString() {
        return "BeanManagerDependencyProvider{beanManager=" + beanManager + "}";
    }
}
