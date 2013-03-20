package com.softwaremill.common.faces.validator;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 * Allow dynamically pass validatorId and delegates validation to validator marked by that id
 * Check README.md in softwaremill-faces for an example
 */
@FacesValidator("delegatingValidator")
public class DelegatingValidator extends AbstractDelgatingValidator { }
