package pl.softwaremill.common.cdi.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Checks if string contains special characters. By default allows only upper and lowercase letters
 * and numbers. Additional characters can be allowed by <code>charsAllowed</code>
 */
@Documented
@Constraint(validatedBy = NoSpecialCharsValidator.class)
@Target( {ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface NoSpecialChars {

    /**
     * Additional allowed characters. Not separated by anything.
     * @return
     */
    String charsAllowed() default "";

    String message() default "Not allowed special characters found";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
    
}
