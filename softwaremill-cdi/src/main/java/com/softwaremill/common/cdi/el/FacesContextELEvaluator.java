package com.softwaremill.common.cdi.el;

import javax.el.ELContext;
import javax.faces.context.FacesContext;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class FacesContextELEvaluator extends AbstractELEvaluator {
    private final FacesContext facesContext;

    public FacesContextELEvaluator(FacesContext facesContext) {
        this.facesContext = facesContext;
    }

    @SuppressWarnings({"unchecked"})
    public <T> T evaluate(String expression, Class<T> expectedResultType) {
        ELContext elContext = facesContext.getELContext();
        return ELEvaluatorUtil.evaluate(elContext, facesContext.getApplication().getExpressionFactory(),
                expression, expectedResultType);
    }

    public void setParameter(String name, Object value) {
        facesContext.getExternalContext().getRequestMap().put(name, value);
    }

    public void clearParameter(String name) {
        facesContext.getExternalContext().getRequestMap().remove(name);
    }
}
