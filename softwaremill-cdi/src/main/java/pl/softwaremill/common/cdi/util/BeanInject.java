package pl.softwaremill.common.cdi.util;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Set;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class BeanInject {
    @SuppressWarnings({"unchecked"})
    public static <T> T lookup(BeanManager manager, Class<T> beanClass) {
        Set<?> beans = manager.getBeans(beanClass);
        if (beans.size() != 1) {
            if (beans.size() == 0) {
                throw new RuntimeException("No beans of class " + beanClass + " found.");
            } else {
                throw new RuntimeException("Multiple beans of class " + beanClass + " found: " + beans + ".");
            }
        }

        Bean<T> myBean = (Bean<T>) beans.iterator().next();

        return (T) manager.getReference(myBean, beanClass, manager.createCreationalContext(myBean));
    }

    @SuppressWarnings({"unchecked"})
    public static <T> T lookup(Class<T> beanClass) {
        return lookup(getBeanManager(), beanClass);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> T lookup(BeanManager manager, String name) {
        Set<?> beans = manager.getBeans(name);
        if (beans.size() != 1) {
            if (beans.size() == 0) {
                throw new RuntimeException("No beans with name " + name + " found.");
            } else {
                throw new RuntimeException("Multiple beans with name " + name + " found: " + beans + ".");
            }
        }

        Bean<T> myBean = (Bean<T>) beans.iterator().next();

        return (T) manager.getReference(myBean, myBean.getBeanClass(), manager.createCreationalContext(myBean));
    }

    @SuppressWarnings({"unchecked"})
    public static <T> T lookup(String name) {
        return BeanInject.<T>lookup(getBeanManager(), name);
    }

    private static BeanManager getBeanManager() {
        try {
            return (BeanManager) new InitialContext().lookup("java:comp/BeanManager");
        } catch (NamingException e) {
            try {
                return (BeanManager) new InitialContext().lookup("java:app/BeanManager");
            } catch (NamingException e1) {
                throw new RuntimeException(e1);
            }

        }
    }
}
