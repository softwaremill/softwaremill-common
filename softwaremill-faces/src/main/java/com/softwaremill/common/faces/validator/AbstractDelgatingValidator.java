package pl.softwaremill.common.faces.validator;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 * Common code for delegating validator
 */
public abstract class AbstractDelgatingValidator implements Validator{

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
