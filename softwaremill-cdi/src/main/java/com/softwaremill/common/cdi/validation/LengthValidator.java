package com.softwaremill.common.cdi.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class LengthValidator implements ConstraintValidator<Length, String> {

    private String tooLongMessage;
    private String tooShortMessage;
    private int min;
    private int max;

    @Override
    public void initialize(Length length) {
        tooLongMessage = length.tooLongMessage();
        tooShortMessage = length.tooShortMessage();
        min = length.min();
        max = length.max();
        validateParameters();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;

        context.disableDefaultConstraintViolation();
        if (value.length() < min) {
            context.buildConstraintViolationWithTemplate(tooShortMessage).addConstraintViolation();

            return false;
        }

        if (value.length() > max) {
            context.buildConstraintViolationWithTemplate(tooLongMessage).addConstraintViolation();

            return false;
        }

        return true;
    }

    private void validateParameters() {
		if ( min < 0 ) {
			throw new IllegalArgumentException( "The min parameter cannot be negative." );
		}
		if ( max < 0 ) {
			throw new IllegalArgumentException( "The max parameter cannot be negative." );
		}
		if ( max < min ) {
			throw new IllegalArgumentException( "The length cannot be negative." );
		}
	}

}
