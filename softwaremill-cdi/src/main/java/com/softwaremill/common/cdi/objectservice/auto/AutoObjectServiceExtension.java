package pl.softwaremill.common.cdi.objectservice.auto;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Extension to process @OS and @OSImpl annotations
 */
public class AutoObjectServiceExtension implements Serializable, Extension {

    Set<Bean> beansToAdd = new HashSet<Bean>();

    Map<Class, Map<Class, Class>> autoOSMap = new HashMap<Class, Map<Class, Class>>();

    public <X> void processAnnotatedType(@Observes ProcessAnnotatedType<X> event, BeanManager beanManager) {

        OS createdWithAnnotation = event.getAnnotatedType().getAnnotation(OS.class);

        if (createdWithAnnotation != null) {
            beansToAdd.add(new AutoOSBean(beanManager, event.getAnnotatedType().getJavaClass(), this));

            // there's never default implementation for this injection point.
            // Veto it and create Bean to be injacted later.
            event.veto();
        }

        OSImpl osImpl = event.getAnnotatedType().getAnnotation(OSImpl.class);

        if (osImpl != null) {

            // but remember the class for it's AutoOS interface
            Class implClass = event.getAnnotatedType().getJavaClass();

            Class autoClass = null;
            Class servicedClass = null;

            for (Type iface : implClass.getGenericInterfaces()) {
                if (iface instanceof ParameterizedType) {
                    ParameterizedType type = (ParameterizedType)iface;

                    Class rawClass = ((Class) type.getRawType());

                    if (rawClass.getAnnotation(OS.class) != null) {
                        if (autoClass != null) {
                            throw new AutoOSException(implClass.getCanonicalName() +
                                    "  implements more then one @OS interface. Found @OS interfaces: " +
                                    autoClass.getCanonicalName() + " , " + rawClass.getCanonicalName());
                        }

                        autoClass = rawClass;

                        servicedClass = ((Class) type.getActualTypeArguments()[0]);
                    }
                }
            }

            if (autoClass == null) {
                throw new AutoOSException("@AutoOSImpl has to implement @AutoOS");
            }

            Map<Class, Class> mapPerAuto;

            if ((mapPerAuto = autoOSMap.get(autoClass)) == null) {
                autoOSMap.put(autoClass, mapPerAuto = new HashMap<Class, Class>());
            }

            mapPerAuto.put(servicedClass, implClass);
        }
    }

    public void afterBeanDiscovery(@Observes AfterBeanDiscovery abd) {
        for (Bean bean : beansToAdd) {
            abd.addBean(bean);
        }
    }
}
