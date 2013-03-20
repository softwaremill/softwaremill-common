package pl.softwaremill.common.faces.validator;

import com.google.common.base.Objects;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import java.util.Iterator;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class FieldsEqualValidator implements javax.faces.validator.Validator {
    public void validate(FacesContext context, UIComponent cmp, Object value)
            throws ValidatorException {
        String compareTo = (String) cmp.getAttributes().get("fieldsEqualCompareTo");
        String messageKey = (String) cmp.getAttributes().get("fieldsEqualMessageKey");

        UIInput input = (UIInput) findComponent(context.getViewRoot(), compareTo);
        String otherValue = (String) input.getValue();

        boolean error = !Objects.equal(value, otherValue);
        if (error) {
            // TODO: internationalize
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, messageKey, messageKey));
        }
    }

    private static UIComponent findComponent(UIComponent component, String id) {
        if (component.getId().equals(id)) {
            return component;
        } else {
            Iterator<UIComponent> children = component.getFacetsAndChildren();
            while(children.hasNext()) {
                UIComponent myComponent = children.next();
                UIComponent componentFound = findComponent(myComponent, id);
                if (componentFound != null) return componentFound;
            }

            return null;
        }
    }
}
