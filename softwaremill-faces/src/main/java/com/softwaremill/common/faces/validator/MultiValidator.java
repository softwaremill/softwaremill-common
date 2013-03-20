package pl.softwaremill.common.faces.validator;

import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
* Created by Pawel Stawicki on Dec 15, 2010 12:44:25 AM
*/
@FacesComponent("MultiValidator")
public class MultiValidator extends UIInput {

    @Override
    public Object getSubmittedValue() {
        List<Object> submittedValues = new ArrayList<Object>();
        for(UIComponent ch : getAllChildren(this)) {
            if (ch instanceof UIInput) {
                submittedValues.add(((UIInput) ch).getSubmittedValue());
            }
        }

        return submittedValues;
    }

    private Collection<UIComponent> getAllChildren(UIComponent component) {
        List<UIComponent> children = new ArrayList<UIComponent>();
        Iterator<UIComponent> it = component.getFacetsAndChildren();
        while(it.hasNext()) {
            UIComponent child = it.next();
            children.add(child);
            children.addAll(getAllChildren(child));
        }

        return children;
    }

    @Override
    public void restoreState(FacesContext context, Object state) {
        List<Object> childrenState = (List<Object>) state;
        for(int i = 0; i < childrenState.size(); i++) {
            getChildren().get(i).restoreState(context, childrenState.get(i));
        }
    }

    @Override
    public Object saveState(FacesContext context) {
        List<Object> childrenState = new ArrayList<Object>();
        for(UIComponent child : getChildren()) {
            childrenState.add(child.saveState(context));
        }

        return childrenState;
    }

    @Override
    public void encodeAll(FacesContext context) throws IOException {
        for(UIComponent child : getChildren()) {
            child.encodeAll(context);
        }
    }

    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        //Do nothing. Let inner components encode themselves, this component does not render to HTML
    }

    @Override
    public void encodeChildren(FacesContext context) throws IOException {
        for(UIComponent child : getChildren()) {
            child.encodeAll(context);
        }
    }

    @Override
    public void encodeEnd(FacesContext context) throws IOException {
        //Do nothing. Let inner components encode themselves, this component does not render to HTML
    }

    @Override
    protected Object getConvertedValue(FacesContext context, Object newSubmittedValue) throws ConverterException {
        return newSubmittedValue;
    }
}
