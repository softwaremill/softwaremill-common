package pl.softwaremill.common.dbtest;

import org.hibernate.ejb.Ejb3Configuration;
import org.jboss.arquillian.api.Deployment;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.inject.Inject;
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
