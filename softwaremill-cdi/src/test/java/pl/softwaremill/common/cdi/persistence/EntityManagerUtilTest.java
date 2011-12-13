package pl.softwaremill.common.cdi.persistence;

import org.hibernate.ejb.Ejb3Configuration;
import org.jboss.arquillian.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.testng.Assert;
import org.testng.annotations.Test;
import pl.softwaremill.common.arquillian.ManifestUtil;

import javax.persistence.EntityManager;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class EntityManagerUtilTest extends AbstractHibernateTest {
    @Override
    protected void configure(Ejb3Configuration cfg) {
        cfg.addAnnotatedClass(SimpleEntity.class);
    }

    @Deployment
    public static JavaArchive createTestArchive() {
        JavaArchive ar = ShrinkWrap.create(JavaArchive.class, "test.jar");
        ar = ManifestUtil.addEmptyBeansXml(ar);

        return ar;
    }

    @Test
    public void testChangeReadOnlyEntity() {
        EntityManager readOnlyEm = newReadOnlyEntityManager();
        EntityManager writeableEm = newEntityManager();

        // Starting a tx
        writeableEm.getTransaction().begin();

        // Writing a new entity
        SimpleEntity se = new SimpleEntity("A");
        writeableEm.persist(se);
        Assert.assertNotNull(se.getId());

        // And commiting the tx (shouldn't flush the RO EM)
        writeableEm.getTransaction().commit();

        // Making a change using a RO EM
        readOnlyEm.getTransaction().begin();

        // Looking up that entity
        SimpleEntity se2 = readOnlyEm.find(SimpleEntity.class, se.getId());
        Assert.assertEquals(se, se2);

        // Changing the entity (read from the RO EM)
        se2.setData("B");

        // And commiting the tx (shouldn't flush the RO EM)
        readOnlyEm.getTransaction().commit();

        // Checking that the changes were not persisted
        readOnlyEm.clear();
        readOnlyEm.getTransaction().begin();
        se2 = readOnlyEm.find(SimpleEntity.class, se.getId());
        Assert.assertEquals(se2.getData(), "A");

        // Closing
        readOnlyEm.close();
        writeableEm.close();
    }
}
