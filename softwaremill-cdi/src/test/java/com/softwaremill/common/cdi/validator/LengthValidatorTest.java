package com.softwaremill.common.cdi.validator;

import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.softwaremill.common.cdi.validation.Length;
import com.softwaremill.common.cdi.validation.LengthValidator;

import javax.validation.ConstraintValidatorContext;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by amorfis on Dec 3, 2010 1:40:58 PM
 */
public class LengthValidatorTest {

    private ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
    private ConstraintValidatorContext.ConstraintViolationBuilder constraintViolationBuilder
            = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);

    @BeforeClass
    private void prepareMocks() {
        constraintViolationBuilder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        when(context.buildConstraintViolationWithTemplate(Mockito.anyString()))
                .thenReturn(constraintViolationBuilder);
    }

    @Test
    public void shouldAllowRegularChars() throws NoSuchFieldException {
        //given
        class AnnotationWrapper {

            @Length(min = 10, max = 15)
            public String field;

        }

        LengthValidator validator = initializeValidator(AnnotationWrapper.class);

        //when
        boolean valid = validator.isValid("PawelStawicki", context);
        boolean tooShort = validator.isValid("Pawel", context);
        boolean tooLong = validator.isValid("Pawellllllllllllllllllllllllllllllll", context);

        //then
        Assert.assertTrue(valid);
        
        Assert.assertFalse(tooShort);
        verify(context).buildConstraintViolationWithTemplate("Value is too short");

        Assert.assertFalse(tooLong);
        verify(context).buildConstraintViolationWithTemplate("Value is too long");
    }

    private LengthValidator initializeValidator(Class<?> classWithAnnotation) throws NoSuchFieldException {
        Length constraint = classWithAnnotation.getField("field").getAnnotation(Length.class);
        LengthValidator validator = new LengthValidator();
        validator.initialize(constraint);

        return validator;
    }

}
