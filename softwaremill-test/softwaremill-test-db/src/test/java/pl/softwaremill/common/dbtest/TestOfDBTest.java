package pl.softwaremill.common.dbtest;

import org.hibernate.ejb.Ejb3Configuration;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pl.softwaremill.common.cdi.persistence.ReadOnly;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.Arrays;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class TestOfDBTest extends AbstractDBTest {
    @Override
    public void configureEntities(Ejb3Configuration cfg) {
        cfg.addAnnotatedClass(TestEntity1.class);
    }

    @Deployment
    public static JavaArchive createTestArchive() {
        return new ArchiveConfigurator() {
            @Override
            protected JavaArchive configureBeans(JavaArchive ar) {
                return ar.addPackage(TestEntity1Manager.class.getPackage());
            }
        }.createTestArchive();
    }

    @Inject
    private TestEntity1Manager manager;

    @Inject
    @ReadOnly
    private EntityManager em;

    @BeforeMethod
    public void beginTransaction() {
        try {
            transactionManager.begin();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @AfterMethod
    public void commitTransaction() {
        try {
            transactionManager.commit();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void beforeClassWithDependencies() {
        super.beforeClassWithDependencies();

        fillDatabase();
    }

    public void fillDatabase() {
        try {
            // transaction is already started

            String sql1 = "insert into test_entity_1(id, data_c) values (0, 'data1');";
            String sql2 = "insert into test_entity_1(id, data_c) values (1, 'data2');";

            em.createNativeQuery(sql1).executeUpdate();
            em.createNativeQuery(sql2).executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testRead() {
        Assert.assertEquals(
                manager.getCurrentData(),
                Arrays.asList(new TestEntity1(0l, "data1"), new TestEntity1(1l, "data2")));
    }

    @Test(dependsOnMethods = "testRead")
    public void testWrite() {
        manager.addEntity(new TestEntity1(2l, "data3"));
    }

    @Test(dependsOnMethods = "testWrite")
    public void testReadAfterWrite() {
        Assert.assertEquals(
                manager.getCurrentData(),
                Arrays.asList(new TestEntity1(0l, "data1"), new TestEntity1(1l, "data2"), new TestEntity1(2l, "data3")));
    }

    @Test
    public void testNoEntityManagerInteraction() {
        // This is on purpose.
    }
}
