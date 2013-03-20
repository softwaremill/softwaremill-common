package com.softwaremill.common.dbtest;

import org.mockito.cglib.proxy.Factory;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Arrays;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import static org.testng.Assert.assertTrue;

/**
 * Extends Mockito with a few helpful methods.
 *
 * @author Maciej Bilas
 * @since 10/20/11 11:42
 */
public class MockitoPersistence {

    private static class LazyValidatorHolder {
        public static Validator instance = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private static Validator getValidator() {
        return LazyValidatorHolder.instance;
    }

    private MockitoPersistence() {
        /* this class should not be instantiated */
    }


    /**
     * Can be used with typical DAO {@code persist(entity)} method.
     * <p/>
     * Validations are run as part of the mocked persist operation. If they fail an assertion error is thrown.
     * <p/>
     * Example usage:
     * {@code when(yourDao.persist(any(BaseEntity.class)).thenAnswer(persistAnswer())}
     * <p/>
     * The Answer assumes that persist method returns the persisted object.
     *
     * @param <T> type of the entities persisted. Just to make the type system happy.
     * @return the {@link Answer} object.
     */
    public static <T> Answer<T> persistAnswer() {
        return new Answer<T>() {
            @Override
            @SuppressWarnings("unchecked")
            public T answer(InvocationOnMock invocation) throws Throwable {
                T entity = (T) invocation.getArguments()[0];
                Set<ConstraintViolation<T>> constraintViolations = getValidator().validate(entity);
                assertTrue(constraintViolations.isEmpty(), format("Entity %s could not be persisted.\n" +
                        "Violations: %s.", entity.toString(), Arrays.toString(constraintViolations.toArray())));
                return (T) invocation.getArguments()[0];
            }
        };
    }

    /**
     * Can be used to mock DAO's reload() method.
     * <p/>
     * Basically this method only returns the parameter passed as the first argument.
     *
     * @param <T> just to make the type system happy
     * @return the {@link Answer} object.
     */
    public static <T> Answer<T> reloadAnswer() {
        return new Answer<T>() {
            @Override
            @SuppressWarnings("unchecked")
            public T answer(InvocationOnMock invocation) throws Throwable {
                T entity = (T) invocation.getArguments()[0];
                return entity;
            }
        };
    }


}
