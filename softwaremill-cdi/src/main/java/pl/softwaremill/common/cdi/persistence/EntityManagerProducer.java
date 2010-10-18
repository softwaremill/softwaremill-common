package pl.softwaremill.common.cdi.persistence;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class EntityManagerProducer {
    @Inject
    private EntityManagerFactory entityManagerFactory;

    @Produces @RequestScoped @Writeable
    public EntityManager getEntityManager() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        return new EntityManagerTxEnlistDecorator(entityManager);
    }

    @Produces @RequestScoped @ReadOnly
    public EntityManager getReadOnlyEntityManager() {
        EntityManager readOnlyEntityManager = entityManagerFactory.createEntityManager();
        EntityManagerUtil.makeEntityManagerReadOnly(readOnlyEntityManager);

        return new EntityManagerTxEnlistDecorator(readOnlyEntityManager);
    }

    public void disposeOfReadOnlyEntityManager(@Disposes @ReadOnly EntityManager readOnlyEntityManager) {
        readOnlyEntityManager.close();
    }

    public void disposeOfWriteableEntityManager(@Disposes @Writeable EntityManager writeableEntityManager) {
        writeableEntityManager.close();
    }
}
