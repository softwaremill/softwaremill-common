package pl.softwaremill.common.cdi.persistence;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class EntityManagerFactoryProducer {
    private static EntityManagerFactory staticEntityManagerFactory;

    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    @Produces
    @RequestScoped
    public EntityManagerFactory getEntityManagerFactory() {
        if (staticEntityManagerFactory != null) {
            return staticEntityManagerFactory;
        }

        return entityManagerFactory;
    }

    /**
     * Set an entity manager factory for testing. 
     * @param staticEntityManagerFactory Entity manager factory that will be returned by this producer.
     */
    public static void setStaticEntityManagerFactory(EntityManagerFactory staticEntityManagerFactory) {
        EntityManagerFactoryProducer.staticEntityManagerFactory = staticEntityManagerFactory;
    }
}
