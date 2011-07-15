package pl.softwaremill.common.faces.validator;

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
public class DelegatingValidator implements Validator {

    protected static final String VALIDATOR_ID = "validatorId";

    @Override
    public void validate(FacesContext facesContext, UIComponent uiComponent, Object value) throws ValidatorException {
        String validatorId = (String) uiComponent.getAttributes().get(VALIDATOR_ID);
        Validator validator = lookup(facesContext, validatorId);
        validator.validate(facesContext, uiComponent, value);
    }

    private Validator lookup(FacesContext facesContext, String validatorId) {
        return facesContext.getApplication().createValidator(validatorId);
    }

}
