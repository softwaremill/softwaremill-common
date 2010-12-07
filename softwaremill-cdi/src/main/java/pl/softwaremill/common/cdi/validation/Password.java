package pl.softwaremill.common.cdi.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Password strength validator.
 *
 * Created by amorfis on Dec 3, 2010 2:08:05 PM
 */
@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target( {ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Password {

    /**
     * Minimal number of lowercase letters required.
     * @return
     */
    int lowercaseRequired() default 0;

    /**
     * Minimal number of uppercase letters required.
     * @return
     */
    int uppercaseRequired() default 0;

    /**
     * Minimal number of digits required.
     * @return
     */
    int digitsRequired() default 0;

    /**
     * Minimal number of special characters required.
     * @return
     */
    int specialRequired() default 0;

    /**
     * Minimal password length
     * @return
     */
    int minLength() default 1;

    String noLowerCaseMessage() default "Not enough lowercase letters";
    String noUpperCaseMessage() default "Not enough uppercase letters";
    String noDigitsMessage() default "Not enough digits";
    String noSpecialMessage() default "Not enough special characters";
    String tooShortMessage() default "Password is too short";

    String message() default "Password too weak";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

}
