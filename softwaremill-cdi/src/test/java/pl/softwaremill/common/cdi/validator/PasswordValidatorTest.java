package pl.softwaremill.common.cdi.validator;

import org.mockito.Mockito;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import pl.softwaremill.common.cdi.validation.Password;
import pl.softwaremill.common.cdi.validation.PasswordValidator;

import javax.validation.ConstraintValidatorContext;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Created by amorfis on Dec 3, 2010 1:40:58 PM
 */
public class PasswordValidatorTest {

    private ConstraintValidatorContext context;
    private ConstraintValidatorContext.ConstraintViolationBuilder constraintViolationBuilder
            = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);

    @BeforeTest
    private void prepareMocks() {
        context = mock(ConstraintValidatorContext.class);

        constraintViolationBuilder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        when(context.buildConstraintViolationWithTemplate(Mockito.anyString()))
                .thenReturn(constraintViolationBuilder);
    }

    @Test
    public void shouldAllowOneChar() throws NoSuchFieldException {
        //given
        class AnnotationWrapper {

            @Password
            public String field;

        }

        PasswordValidator validator = initializeValidator(AnnotationWrapper.class);

        //when
        boolean oneLetter = validator.isValid("a", context);
        boolean oneNumber = validator.isValid("2", context);
        boolean oneSpecial = validator.isValid("*", context);
        boolean strongPass = validator.isValid("*Pawel$##*Stawicki))", context);

        //then
        assertTrue(oneLetter, "One letter considered invalid");
        assertTrue(oneNumber, "One number considered invalid");
        assertTrue(oneSpecial, "One special char considered invalid");
        assertTrue(strongPass, "Strong password considered invalid");
    }

    private PasswordValidator initializeValidator(Class<?> classWithAnnotation) throws NoSuchFieldException {
        Password constraint = classWithAnnotation.getField("field").getAnnotation(Password.class);
        PasswordValidator validator = new PasswordValidator();
        validator.initialize(constraint);

        return validator;
    }

    @Test
    public void shouldRequireUpperAndLowerCase() throws NoSuchFieldException {
        //given
        class AnnotationWrapper {

            @Password(lowercaseRequired = 1, uppercaseRequired = 1)
            public String field;

        }

        PasswordValidator validator = initializeValidator(AnnotationWrapper.class);

        //when
        boolean lowerAndUpper = validator.isValid("aA", context);
        boolean number = validator.isValid("2", context);
        boolean special = validator.isValid("*", context);
        boolean oneLower = validator.isValid("a", context);
        boolean oneUpper = validator.isValid("A", context);

        //then
        assertTrue(lowerAndUpper, "Upper and lowercase considered invalid");
        assertFalse(number, "One number considered valid");
        assertFalse(special, "One special char considered valid");
        assertFalse(oneLower, "One lowercase letter considered valid");
        assertFalse(oneUpper, "One uppercase letter considered valid");
    }

    @Test
    public void shouldAddErrorMessageWhenNoRequiredLowercase() throws NoSuchFieldException {
        //given
        class AnnotationWrapper {

            @Password(lowercaseRequired = 1, uppercaseRequired = 1)
            public String field;

        }

        PasswordValidator validator = initializeValidator(AnnotationWrapper.class);

        //when
        boolean isValid = validator.isValid("2", context);

        //then
        verify(context).buildConstraintViolationWithTemplate(eq("Not enough lowercase letters"));
        verify(constraintViolationBuilder).addConstraintViolation();

    }

    @Test
    public void shouldRequire3Numbers() throws NoSuchFieldException {
        //given
        class AnnotationWrapper {

            @Password(digitsRequired = 3)
            public String field;

        }

        PasswordValidator validator = initializeValidator(AnnotationWrapper.class);

        //when
        boolean threeNumbers = validator.isValid("123", context);
        boolean noNumbers = validator.isValid("assdfSDFSDD#$%DDD", context);

        //then
        assertTrue(threeNumbers, "Three numbers considered invalid");
        assertFalse(noNumbers, "No numbers considered valid");
    }

    @Test
    public void shouldRequireStrongPassword() throws NoSuchFieldException {
        //given
        class AnnotationWrapper {

            @Password(
                    lowercaseRequired = 1,
                    uppercaseRequired = 1,
                    digitsRequired = 1,
                    specialRequired = 1,
                    minLength = 10)
            public String field;

        }

        PasswordValidator validator = initializeValidator(AnnotationWrapper.class);

        //when
        boolean strongPass = validator.isValid("aAabaA*A22", context);
        boolean shortPass = validator.isValid("aA*2aaaad", context);
        boolean noSpecials = validator.isValid("aaaaAAAA2222", context);
        boolean noLowercase = validator.isValid("BBBBA.AA2*22", context);
        boolean noUppercase = validator.isValid("bbbba.aa2*22", context);
        boolean noNumbers = validator.isValid("asdfsADSSD***", context);

        //then
        assertTrue(strongPass, "Strong password considered invalid");
        assertFalse(shortPass, "Too short password considered valid");
        assertFalse(noSpecials, "Password without special char considered valid");
        assertFalse(noLowercase, "Password without lowercase letter considered valid");
        assertFalse(noUppercase, "Password without uppercase letter considered valid");
        assertFalse(noNumbers, "Password without number considered valid");
    }

}
