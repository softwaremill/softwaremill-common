package pl.softwaremill.common.cdi.persistence;

import org.hibernate.proxy.HibernateProxy;
import pl.softwaremill.common.util.persistance.Identifiable;

import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 * @author Adam Warski (adam at warski dot org)
 * @author Tomasz Szymanski (szimano at szimano dot org)
 * @author Pawel Stawicki (pawelstawicki at gmail dot com)
 */
public class EntityWriter {
    @Inject @ReadOnly
    private EntityManager readOnlyEm;

    @Inject @Writeable
    private EntityManager writeableEm;

    @SuppressWarnings({"unchecked"})
    /**
     * Stores changes made to an entity. All entites must have {@code DETACH} cascade enabled on all associations!
     * @param entity The entity to be written.
     * @return The written entity.
     */
    public <T extends Identifiable<?>> T write(T entity) {
        // First detaching the entity from the RO context
        if (readOnlyEm.contains(entity)) {
            readOnlyEm.detach(entity);
        } else if (entity.getId() != null) {
            // If the entity is not in the RO EM, and is persistent, it is possible that the RO EM contains a different
            // copy of the entity. It must also be detached, hence first looking it up. It is possible that the find()
            // loads the entity into the EM, but it's not possible to check if an entity is loaded into an EM simply
            // by id.
            readOnlyEm.detach(readOnlyEm.find(entity.getClass(), entity.getId()));
        }
        // Writing the changes
        T writtenEntity = writeableEm.merge(entity);
        writeableEm.flush();
        // Now looking up a fresh copy of the entity. We won't get a stale one because we removed it from the context
        // in the beginning
        return (T) readOnlyEm.find(writtenEntity.getClass(), writtenEntity.getId());
    }

    /**
     * Executes an update query, created with the given creator, using the writeable entity manager.
     * The read only entity manager is cleared, so all entities with lazy values will have to be re-read.
     * @param queryCreator A creator to create the update query.
     * @return The number of affected rows.
     */
    public int executeUpdate(QueryCreator queryCreator) {
        int result = queryCreator.createQuery(writeableEm).executeUpdate();

        // Now we need to clear the read only entity manager, so that it doesn't contain stale entities.
        // This could result in some lazy initialization exception.
        readOnlyEm.clear();

        return result;
    }

    /**
     * Deletes an entity and detaches it from the read only manager.
     * @param entity Entity to delete
     */
    @SuppressWarnings({"unchecked"})
    public <T extends Identifiable<?>> void delete(T entity) {
        // First detaching the entity from the RO context
        if (readOnlyEm.contains(entity)) {
            readOnlyEm.detach(entity);
        } else if (entity.getId() != null) {
            // If the entity is not in the RO EM, and is persistent, it is possible that the RO EM contains a different
            // copy of the entity. It must also be detached, hence first looking it up. It is possible that the find()
            // loads the entity into the EM, but it's not possible to check if an entity is loaded into an EM simply
            // by id.
            Class<T> entityTargetClass = getTargetClassIfProxied((Class<T>) entity.getClass());
            readOnlyEm.detach(readOnlyEm.find(entityTargetClass, entity.getId()));
        }

        // attach the entity
        entity = (T) writeableEm.find(entity.getClass(), entity.getId());

        // Then delete the entity
        writeableEm.remove(entity);
        writeableEm.flush();
    }

    private <T> Class<T> getTargetClassIfProxied(Class<T> clazz) {
        if (clazz == null) {
            return null;
        } else if (HibernateProxy.class.isAssignableFrom(clazz)) {
            // Get the source class of Javassist proxy instance.
            return (Class<T>) clazz.getSuperclass();
        }
        return clazz;
    }

}
