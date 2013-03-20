package pl.softwaremill.common.cdi.persistence;

import org.hibernate.ejb.Ejb3Configuration;
import org.hibernate.proxy.HibernateProxy;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.testng.Assert;
import org.testng.annotations.Test;
import pl.softwaremill.common.arquillian.ManifestUtil;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import static org.fest.assertions.Assertions.assertThat;
import static org.testng.Assert.assertNull;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class EntityWriterTest extends AbstractHibernateTest {
    @Override
    protected void configure(Ejb3Configuration cfg) {
        cfg.addAnnotatedClass(SimpleEntity.class);
        cfg.addAnnotatedClass(EntityWithLazySubentity.class);
        // Setting isolation to TRANSACTION_READ_UNCOMMITTED as JTA is not available and using two EMs causes otherwise
        // deadlocks.
        cfg.setProperty("hibernate.connection.isolation", "1");
    }

    @Deployment
    public static JavaArchive createTestArchive() {
        JavaArchive ar = ShrinkWrap.create(JavaArchive.class, "test.jar")
                .addClass(EntityWriter.class)
                .addClass(MockEntityManagerProducers.class);

        ar = ManifestUtil.addEmptyBeansXml(ar);

        return ar;
    }

    @Inject
    private EntityWriter entityWriter;

    @Inject @ReadOnly
    private EntityManagerDelegator readOnlyEntityManagerDelegator;

    @Inject @Writeable
    private EntityManagerDelegator writeableEntityManagerDelegator;

    @Test
    public void testPersistWrite() {
        // Creating and setting the entity managers
        EntityManager readOnlyEm = newReadOnlyEntityManager();
        EntityManager writeableEm = newEntityManager();

        readOnlyEntityManagerDelegator.setDelegate(readOnlyEm);
        writeableEntityManagerDelegator.setDelegate(writeableEm);

        // Writing an entity
        writeableEm.getTransaction().begin();
        readOnlyEm.joinTransaction();

        SimpleEntity se = new SimpleEntity("A");
        se = entityWriter.write(se);

        Assert.assertEquals(se.getData(), "A");
        Assert.assertNotNull(se.getId());

        // Trying to read the entity
        Assert.assertEquals(readOnlyEm.find(SimpleEntity.class, se.getId()), se);

        writeableEm.getTransaction().commit();

        // Reading the entity using a different EM
        EntityManager readOnlyEm2 = newReadOnlyEntityManager();
        SimpleEntity se2 = readOnlyEm2.find(SimpleEntity.class, se.getId());
        Assert.assertEquals(se, se2);

        // Closing
        readOnlyEm.close();
        readOnlyEm2.close();
        writeableEm.close();
    }

    @Test
    public void testUpdateDetachedWhichIsInReadOnlyEM() {
        // Creating and setting the entity managers
        EntityManager readOnlyEm = newReadOnlyEntityManager();
        EntityManager writeableEm = newEntityManager();

        readOnlyEntityManagerDelegator.setDelegate(readOnlyEm);
        writeableEntityManagerDelegator.setDelegate(writeableEm);

        // Writing an entity
        writeableEm.getTransaction().begin();
        readOnlyEm.joinTransaction();

        SimpleEntity se = new SimpleEntity("A");
        se = entityWriter.write(se);

        writeableEm.getTransaction().commit();

        // Simulating a new request - new entity managers
        readOnlyEm.close();
        writeableEm.close();

        EntityManager readOnlyEm2 = newReadOnlyEntityManager();
        EntityManager writeableEm2 = newEntityManager();

        readOnlyEntityManagerDelegator.setDelegate(readOnlyEm2);
        writeableEntityManagerDelegator.setDelegate(writeableEm2);

        // Loading the entity into the new RO EM
        Assert.assertEquals(readOnlyEm2.find(SimpleEntity.class, se.getId()), se);

        // Writing the (now detached, as the old EMs are closed) modified entity
        writeableEm2.getTransaction().begin();
        se.setData("B");
        se = entityWriter.write(se);

        Assert.assertEquals(se.getData(), "B");

        writeableEm2.getTransaction().commit();

        // Closing
        readOnlyEm2.close();
        writeableEm2.close();
    }

    @Test
    public void testPersistDelete() {
        // Creating and setting the entity managers
        EntityManager readOnlyEm = newReadOnlyEntityManager();
        EntityManager writeableEm = newEntityManager();

        readOnlyEntityManagerDelegator.setDelegate(readOnlyEm);
        writeableEntityManagerDelegator.setDelegate(writeableEm);

        // Writing an entity
        writeableEm.getTransaction().begin();
        readOnlyEm.joinTransaction();

        SimpleEntity se = new SimpleEntity("C");
        se = entityWriter.write(se);

        Assert.assertEquals(se.getData(), "C");
        Assert.assertNotNull(se.getId());

        // Trying to read the entity
        Assert.assertEquals(readOnlyEm.find(SimpleEntity.class, se.getId()), se);

        writeableEm.getTransaction().commit();

        // Writing an entity
        writeableEm.getTransaction().begin();
        readOnlyEm.joinTransaction();
        
        // now delete the entity
        entityWriter.delete(se);

        writeableEm.getTransaction().commit();

        // check it's not accessible by the read only manager
        assertNull(readOnlyEm.find(SimpleEntity.class, se.getId()));

        // Closing
        readOnlyEm.close();
        writeableEm.close();
    }

    @Test
    public void shouldPersistEntityProxyEvenIfNotInWritableEmContext() {
        // Creating and setting the entity managers
        EntityManager readOnlyEm = newReadOnlyEntityManager();
        EntityManager writeableEm = newEntityManager();

        readOnlyEntityManagerDelegator.setDelegate(readOnlyEm);
        writeableEntityManagerDelegator.setDelegate(writeableEm);

        // Write an entity with proxy
        EntityWithLazySubentity entityWithLazy = new EntityWithLazySubentity();
        entityWithLazy.setSubentity(new SimpleEntity());

        // Write to the DB
        writeableEm.getTransaction().begin();
        readOnlyEm.joinTransaction();

        // Write entity with proxy
        EntityWithLazySubentity writtenEntity = entityWriter.write(entityWithLazy);

        writeableEm.getTransaction().commit();

        readOnlyEm.clear();
        writeableEm.clear();

        // Write to the DB
        writeableEm.getTransaction().begin();
        readOnlyEm.joinTransaction();

        // Get proxy
        writtenEntity = readOnlyEm.find(EntityWithLazySubentity.class, writtenEntity.getId());
        assertThat(writtenEntity).isNotNull();

        SimpleEntity proxy = writtenEntity.getSubentity();
        assertThat(proxy).isNotNull();

        // Try to write the proxy
        SimpleEntity writeResult = entityWriter.write(proxy);

        assertThat(writeResult).isNotNull();
        assertThat(writeResult instanceof HibernateProxy).isFalse();
    }

}
