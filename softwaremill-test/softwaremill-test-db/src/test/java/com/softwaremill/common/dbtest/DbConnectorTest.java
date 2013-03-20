package com.softwaremill.common.dbtest;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.softwaremill.common.dbtest.util.DbMode;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Pawel Stawicki
 * @since 7/27/12 1:59 PM
 */
public class DbConnectorTest {

    private static final long ID = 1L;
    private static final long ID2 = 2L;

    private EntityManagerFactory emf;

    @BeforeClass
    private void createEntityManagerFactoryFromDbConnector() {
        DbConnector connector = new DbConnector();

        connector.getConfiguration().addAnnotatedClass(TestEntity1.class);
        connector.setCompatibilityMode(DbMode.DB2);

        emf = connector.createEntityManagerFactory();
    }

    @Test
    public void shouldNotSeeChangesInUncommitedTransactionFromAnotherTransaction() {
        // Given
        EntityManager em1 = emf.createEntityManager();
        EntityManager em2 = emf.createEntityManager();

        // When
        em1.getTransaction().begin();
        TestEntity1 entity = new TestEntity1();
        entity.setId(ID);
        em1.persist(entity);

        em2.getTransaction().begin();
        TestEntity1 entityFromSecondTransaction = em2.find(TestEntity1.class, ID);

        // Then
        assertThat(entityFromSecondTransaction).isNull();

        // Cleanup
        em1.getTransaction().rollback();
        em2.getTransaction().rollback();
    }

    @Test
    public void shouldSeeChangesInCommitedTransactionFromAnotherTransaction() {
        // Given
        EntityManager em1 = emf.createEntityManager();
        EntityManager em2 = emf.createEntityManager();

        // When
        em1.getTransaction().begin();
        TestEntity1 entity = new TestEntity1();
        entity.setId(ID2);
        em1.persist(entity);
        em1.getTransaction().commit();

        em2.getTransaction().begin();
        TestEntity1 entityFromSecondTransaction = em2.find(TestEntity1.class, ID2);

        // Then
        assertThat(entityFromSecondTransaction).isNotNull();

        // Cleanup
        em2.getTransaction().rollback();
    }

}
