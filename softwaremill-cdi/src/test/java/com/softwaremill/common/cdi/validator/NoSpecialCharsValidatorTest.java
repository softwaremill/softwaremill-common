package com.softwaremill.common.cdi.validator;

import org.testng.Assert;
import org.testng.annotations.Test;
import com.softwaremill.common.cdi.validation.NoSpecialChars;
import com.softwaremill.common.cdi.validation.NoSpecialCharsValidator;

/**
 * Created by amorfis on Dec 3, 2010 1:40:58 PM
 */
public class NoSpecialCharsValidatorTest {

    @Test
    public void shouldAllowRegularChars() throws NoSuchFieldException {
        //given
        class AnnotationWrapper {

            @NoSpecialChars
            public String field;

        }

        NoSpecialCharsValidator validator = initializeValidator(AnnotationWrapper.class);

        //when
        boolean isValid = validator.isValid("PawelStawicki", null);

        //then
        Assert.assertTrue(isValid, "Regular characters considered invalid");
    }

    private NoSpecialCharsValidator initializeValidator(Class<?> classWithAnnotation) throws NoSuchFieldException {
        NoSpecialChars constraint = classWithAnnotation.getField("field").getAnnotation(NoSpecialChars.class);
        NoSpecialCharsValidator validator = new NoSpecialCharsValidator();
        validator.initialize(constraint);

        return validator;
    }

    @Test
    public void shouldNotAllowSpecialChars() throws NoSuchFieldException {
        //given
        class AnnotationWrapper {

            @NoSpecialChars
            public String field;

        }

        NoSpecialCharsValidator validator = initializeValidator(AnnotationWrapper.class);

        //when
        boolean manySpecials = validator.isValid("*%%Pawel@#$Stawicki**", null);
        boolean dot = validator.isValid("Pawel.Stawicki", null);
        boolean at = validator.isValid("Pawel@Stawicki", null);

        //then
        Assert.assertFalse(manySpecials, "Special characters considered valid");
        Assert.assertFalse(dot, "Special characters considered valid");
        Assert.assertFalse(at, "Special characters considered valid");
    }

    @Test
    public void shouldAllowSomeSpecialCharsWhenAsked() throws NoSuchFieldException {
        //given
        class AnnotationWrapper {

            @NoSpecialChars(charsAllowed = "@.")
            public String field;

        }

        NoSpecialCharsValidator validator = initializeValidator(AnnotationWrapper.class);

        //when
        boolean isValid = validator.isValid("Pawel@Stawicki.pl", null);

        //then
        Assert.assertTrue(isValid, "Special characters which should be valid considered valid");
    }

}
