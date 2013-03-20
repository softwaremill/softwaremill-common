package pl.softwaremill.common.cdi.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Checks if length is greater than {@code min}, and less than {@code max}.
 * Two different error messages for strings that are either too short or two long can be supplied.
 */
@Documented
@Constraint(validatedBy = LengthValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Length {

    String tooLongMessage() default "Value is too long";

    String tooShortMessage() default "Value is too short";

    int min() default 0;

    int max() default Integer.MAX_VALUE;

    String message() default "Something is wrong";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
