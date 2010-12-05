package pl.softwaremill.common.cdi.autofactory.extension;

import pl.softwaremill.common.cdi.autofactory.CreatedWith;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class AutoFactoryExtension implements Extension {
    private Set<Bean> factoryBeansToAdd = new HashSet<Bean>();

    public <X> void processAnnotatedType(@Observes ProcessAnnotatedType<X> event, BeanManager beanManager) {
        CreatedWith createdWithAnnotation = event.getAnnotatedType().getAnnotation(CreatedWith.class);
        if (createdWithAnnotation != null) {
            factoryBeansToAdd.add(new AutoFactoryFromCreatedWithCreator<X>(beanManager, createdWithAnnotation, event)
                    .create());

            // This class shouldn't be treated normally, as it contains a constructor with @Inject, where
            // not all of the arguments can be injected from the bean manager.
            event.veto();
        }
    }

    public void afterBeanDiscovery(@Observes AfterBeanDiscovery abd) {
        for (Bean bean : factoryBeansToAdd) {
            abd.addBean(bean);
        }
    }
}
