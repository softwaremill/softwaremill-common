package pl.softwaremill.common.cdi.persistence;

import org.hibernate.FlushMode;
import org.hibernate.Session;

import javax.persistence.EntityManager;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class EntityManagerUtil {
    /**
     * Changes the given entity manager to be read only. No changes will be flushed automatically. Also all entities
     * will be marked as read only by Hibernate.
     * @param em The entity manager to make read only.
     */
    public static void makeEntityManagerReadOnly(EntityManager em) {
        Session readOnlySession = (Session) em.getDelegate();
        readOnlySession.setDefaultReadOnly(true);
        readOnlySession.setFlushMode(FlushMode.MANUAL);
    }
}
