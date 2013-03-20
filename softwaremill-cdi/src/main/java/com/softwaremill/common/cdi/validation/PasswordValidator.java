package com.softwaremill.common.cdi.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by amorfis on Dec 3, 2010 2:10:58 PM
 */
public class PasswordValidator implements ConstraintValidator<Password, String> {

    private int lowercaseRequired;
    private int uppercaseRequired;
    private int numbersRequired;
    private int specialRequired;
    private int minLength;

    private String noLowerCaseMessage;
    private String noUpperCaseMessage;

    private Pattern lowercasePattern = Pattern.compile("[a-z]");
    private Pattern uppercasePattern = Pattern.compile("[A-Z]");
    private Pattern numbersPattern = Pattern.compile("[0-9]");
    private Pattern specialPattern = Pattern.compile("[^A-Za-z0-9]");
    private String noNumbersMessage;
    private String noSpecialMessage;
    private String tooShortMessage;

    public void initialize(Password constraint) {
        lowercaseRequired = constraint.lowercaseRequired();
        uppercaseRequired = constraint.uppercaseRequired();
        numbersRequired = constraint.digitsRequired();
        specialRequired = constraint.specialRequired();
        minLength = constraint.minLength();

        noLowerCaseMessage = constraint.noLowerCaseMessage();
        noUpperCaseMessage = constraint.noUpperCaseMessage();
        noNumbersMessage = constraint.noDigitsMessage();
        noSpecialMessage = constraint.noSpecialMessage();
        tooShortMessage = constraint.tooShortMessage();
    }

    public boolean isValid(String pass, ConstraintValidatorContext context) {
        if (pass == null) return true;

        ConstraintsMatcher builder = new ConstraintsMatcher(pass, context)
                .check(lowercasePattern, lowercaseRequired, noLowerCaseMessage)
                .check(uppercasePattern, uppercaseRequired, noUpperCaseMessage)
                .check(numbersPattern, numbersRequired, noNumbersMessage)
                .check(specialPattern, specialRequired, noSpecialMessage)
                .checkLength(minLength, tooShortMessage);

        return builder.matches();
    }

    private static class ConstraintsMatcher {

        private String password;
        private ConstraintValidatorContext context;

        private boolean matches = true;

        public ConstraintsMatcher(String password, ConstraintValidatorContext context) {
            this.password = password;
            this.context = context;
        }

        public ConstraintsMatcher hasMinLength(int minLength) {
            if (matches) {
                matches = password.length() >= minLength;
            }

            return this;
        }

        public boolean matches() {
            return matches;
        }

        public ConstraintsMatcher check(Pattern pattern, int occurenciesRequired, String errorMessage) {
            if (matches && occurenciesRequired > 0) {

                Matcher m = pattern.matcher(password);

                int count = 0;
                while (m.find()) {
                    count++;
                }

                if (count < occurenciesRequired) {
                    createError(errorMessage);
                }
            }

            return this;
        }

        public ConstraintsMatcher checkLength(int minLength, String errorMessage) {
            if (matches && minLength > 0) {
                if (password.length() < minLength) {
                    createError(errorMessage);
                }
            }

            return this;
        }

        private void createError(String message) {
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            matches = false;
        }
    }

}
