package pl.softwaremill.common.cdi.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * Created by amorfis on Dec 3, 2010 1:34:27 PM
 */
public class NoSpecialCharsValidator implements ConstraintValidator<NoSpecialChars, String> {

    Pattern regex;

    public void initialize(NoSpecialChars constraint) {
        String charsAllowed = constraint.charsAllowed();
        regex = Pattern.compile("^[A-Za-z0-9" + charsAllowed + "]*$");
    }

    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        
        return regex.matcher(value).matches();
    }
}
