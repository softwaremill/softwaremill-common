package pl.softwaremill.common.dbtest;

import org.hibernate.ejb.Ejb3Configuration;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.persistence.ShouldMatchDataSet;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.testng.annotations.Test;
import pl.softwaremill.common.cdi.persistence.EntityWriter;
import pl.softwaremill.common.cdi.persistence.ReadOnly;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * User: szimano
 */
public class TestOfDBTestWithArqPersistence extends AbstractDBTest {

    public TestOfDBTestWithArqPersistence() {
        super(true);
    }

    @Override
    protected void loadTestData(EntityManager em) throws IOException {
        // do nothing
    }

    @Override
    public void configureEntities(Ejb3Configuration cfg) {
        cfg.addAnnotatedClass(TestEntity1.class);
    }

    @Deployment
    public static JavaArchive createTestArchive() {
        return new ArchiveConfigurator() {
            @Override
            protected JavaArchive configureBeans(JavaArchive ar) {
                return ar;
            }
        }.createTestArchive();
    }

    @Inject
    @ReadOnly
    private EntityManager entityManager;

    @Inject
    private EntityWriter entityWriter;

    @Test
    @UsingDataSet("entities.yml")
    @ShouldMatchDataSet("entities_after.yml")
    public void testReadAndDeleteWithUsingAndShouldMatch() {
        TestEntity1 enitity = entityManager.find(TestEntity1.class, 1l);

        entityWriter.delete(enitity);
    }

    @Test
    @UsingDataSet("entities.yml")
    public void testReadWithUsing() {
        List list = entityManager.createQuery("select e from TestEntity1 e").getResultList();

        assertThat(list.size()).isEqualTo(2);
    }

    @Test
    public void testNoEntityManagerInteraction() {
        // This is on purpose.
    }
}
