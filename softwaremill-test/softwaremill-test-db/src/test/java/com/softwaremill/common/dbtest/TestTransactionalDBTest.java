package com.softwaremill.common.dbtest;

import org.hibernate.ejb.Ejb3Configuration;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.testng.annotations.Test;
import com.softwaremill.common.cdi.persistence.EntityWriter;
import com.softwaremill.common.cdi.persistence.ReadOnly;
import com.softwaremill.common.util.dependency.D;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Simple test to demonstrate how to use TransactionalDBTest
 */
@Test(dependsOnGroups = "TestOfDbTest")
public class TestTransactionalDBTest extends TransactionalDBTest {

    @Inject
    private EntityWriter writer;

    @Inject
    @ReadOnly
    private EntityManager em;

    @Override
    protected void configureEntities(Ejb3Configuration cfg) {
        cfg.addAnnotatedClass(TransactionalEntity.class);
    }

    @Override
    protected void loadTestData(EntityManager em) throws Exception {
        beginTransaction();
        em.joinTransaction();

        TransactionalEntity entity1 = new TransactionalEntity("Some data");
        em.persist(entity1);

        commitTransaction();
        em.close();
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

    @Test
    public void shouldPersistDataWithTransaction() throws Exception {
        // given
        TransactionalEntity e1 = new TransactionalEntity("Important Data");

        // when
        beginTransaction();

        try {
            e1 = writer.write(e1);
            commitTransaction();
        } catch (Exception e) {
            rollbackTransaction(); // allow TM to shutdown
            throw e;
        }

        beginTransaction();
        TransactionalEntity persistedEntity;
        try {
            persistedEntity = em.find(e1.getClass(), e1.getId());
            commitTransaction();
        } catch (Exception e) {
            rollbackTransaction(); // allow TM to shutdown
            throw e;
        }

        // then
        assertThat(persistedEntity).isEqualTo(e1);
    }

    @Test
    public void shouldRollbackSavedData() throws Exception {
        // given
        beginTransaction();
        TransactionalEntity e1 = writer.write(new TransactionalEntity("Data1"));
        commitTransaction();

        beginTransaction();
        TransactionalEntity e2 = em.find(e1.getClass(), e1.getId());
        commitTransaction();

        assertThat(e1).isEqualTo(e2);

        // when
        beginTransaction();
        try {
            TransactionalEntity e3 = writer.write(new TransactionalEntity("Data2"));
            TransactionalEntity e4 = em.find(e3.getClass(), e3.getId());
            assertThat(e3).isEqualTo(e4);

            throw new IllegalArgumentException("Bad value!");
        } catch (IllegalArgumentException e) {
            rollbackTransaction();
        }

        // then
        List<TransactionalEntity> resultList;
        beginTransaction();
        try {
            resultList = em.createQuery("from " + TransactionalEntity.class.getName(), TransactionalEntity.class).getResultList();
            commitTransaction();
        } catch (Exception e) {
            rollbackTransaction();
            throw e;
        }

        assertThat(resultList).hasSize(1);
        assertThat(resultList.get(0)).isEqualTo(e1);
    }

    /**
     * UserTransaction are only supported via D.inject(), as in other case Weld will inject some dummy implementation
     *
     * @throws Exception
     */
    @Test
    public void shouldPersistDataWithUserTransaction() throws Exception {
        // given
        UserTransaction utx = D .inject(UserTransaction.class);

        // when
        TransactionalEntity e1;
        try {
            utx.begin();
            e1 = writer.write(new TransactionalEntity("UTX"));
            utx.commit();
        } catch (Exception e) {
            utx.rollback(); // allow TM to shutdown
            throw e;
        }

        // then
        beginTransaction();
        TransactionalEntity e2 = em.find(e1.getClass(), e1.getId());
        commitTransaction();

        assertThat(e1).isEqualTo(e2);
    }

    @Override
    protected void callBeforeMethod() throws Exception {
        beginTransaction();
        try {
            List<TransactionalEntity> resultList = em.createQuery("from " + TransactionalEntity.class.getName(), TransactionalEntity.class).getResultList();
            for (TransactionalEntity entity : resultList) {
                writer.delete(entity);
            }
            commitTransaction();
        } catch (Exception e) {
            rollbackTransaction(); // allow TM to shutdown
            throw e;
        }
    }

}
