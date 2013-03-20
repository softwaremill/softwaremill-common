package com.softwaremill.common.util.validation;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.Pattern;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * Checks that a string is not empty and has at least one non-whitespace character - so string is not empty after trim.
 *
 * @author maciek
 */
@Documented
@Constraint(validatedBy = { })
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@NotEmpty
@Pattern(regexp = "(?s).*?\\S+.*") // (?s) makes . match all - also newlines
public @interface NotJustWhitespace {
    String message() default "{com.softwaremill.common.validator.NotJustWhitespace.message}";

    Class<?>[] groups() default { };

	Class<? extends Payload>[] payload() default { };
}


