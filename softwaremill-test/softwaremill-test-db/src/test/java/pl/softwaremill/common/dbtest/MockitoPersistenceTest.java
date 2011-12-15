package pl.softwaremill.common.dbtest;

import org.testng.annotations.Test;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;
import static pl.softwaremill.common.dbtest.MockitoPersistence.*;

/**
 * @author Maciej Bilas
 * @since 15/12/11 11:57
 */
public class MockitoPersistenceTest {

    @Test
    public void persistAnswerShouldRunValidations() {
        try {
            EntityDAO mock = mock(EntityDAO.class);
            when(mock.persist(isA(Entity.class))).thenAnswer(persistAnswer());
            mock.persist(new Entity());
        } catch (AssertionError e) {
            return;
        }
        fail("Validations did not run successfully.");
    }

    @Test
    public void persistAnswerShouldReturnTheSameObjectWhenValidationsSucceed() {
        // Given
        EntityDAO mock = mock(EntityDAO.class);
        when(mock.persist(isA(Entity.class))).thenAnswer(persistAnswer());
        Entity prePersist = new Entity("foo");

        // When
        Entity postPersist = mock.persist(prePersist);

        // Then
        assertSame(postPersist, prePersist);
    }

    @Test
    public void reloadAnswerShouldReturnTheSameObject() {
        // Given
        EntityDAO mock = mock(EntityDAO.class);
        when(mock.reload(isA(Entity.class))).thenAnswer(reloadAnswer());
        Entity preReload = new Entity();

        // When
        Entity postReload = mock.reload(preReload);

        // Then
        assertSame(postReload, preReload);
    }

}

