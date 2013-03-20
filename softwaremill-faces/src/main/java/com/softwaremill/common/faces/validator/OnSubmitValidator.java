package pl.softwaremill.common.faces.validator;

import com.google.common.base.Strings;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Validator that will only perform validation if f:param performValidation is set to "true".
 * 
 * You can pass validatorId and/or required = true
 */
@FacesValidator("onSubmitValidator")
public class OnSubmitValidator extends AbstractDelgatingValidator {

    private static final String PERFORM_VALIDATION = "performValidation";
    private static final String REQUIRED = "onSubmitRequired";
    private static final String REQUIRED_MESSAGE = "onSubmitRequiredMessage";
    private static final String TRUE = "true";


    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (!performValidationParameterSet(context)) {
            return;
        }

        Set<FacesMessage> messages = new HashSet<FacesMessage>();

        if (TRUE.equals(component.getAttributes().get(REQUIRED))) {
            // make it just required
            if (valueIsEmpty(value)) {
                String requiredMessage = "Value is required !";
                if (component.getAttributes().get(REQUIRED_MESSAGE) != null) {
                    requiredMessage = component.getAttributes().get(REQUIRED_MESSAGE).toString();
                }
                FacesMessage message = new FacesMessage(requiredMessage);

                message.setSeverity(FacesMessage.SEVERITY_ERROR);
                messages.add(message);
            }
        }

        if (component.getAttributes().get(VALIDATOR_ID) != null) {
            // otherwise call the validator
            try {
                super.validate(context, component, value);
            } catch (ValidatorException e) {
                // retrieve messages
                messages.addAll(e.getFacesMessages());
            }
        }

        // if there are any messages - throw validation exception
        if (!messages.isEmpty()) {
            throw new ValidatorException(messages);
        }
    }


    private boolean performValidationParameterSet(FacesContext context) {
        return TRUE.equals(
                   ((HttpServletRequest) context.getExternalContext().getRequest()).getParameter(PERFORM_VALIDATION));
    }


    private boolean valueIsEmpty(Object value) {
        return value == null || (value instanceof String && Strings.isNullOrEmpty((String) value)) ||
               (value instanceof Collection && ((Collection) value).isEmpty());
    }

}