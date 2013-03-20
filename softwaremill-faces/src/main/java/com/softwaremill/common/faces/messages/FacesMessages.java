package com.softwaremill.common.faces.messages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.softwaremill.common.cdi.el.ELEvaluator;
import com.softwaremill.common.faces.i18n.CurrentLocale;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author Seam2 authors (http://seamframework.org)
 * @author Adam Warski (adam at warski dot org)
 */
@SessionScoped
// TODO: make @FlashScoped after flash is fixed
public class FacesMessages implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(FacesMessages.class);

    @Inject
    private ELEvaluator elEvaluator;

    @Inject
    private CurrentLocale currentLocale;

    private static class MessageData {
        private final String controlId;
        private final FacesMessage fm;

        private MessageData(String controlId, FacesMessage fm) {
            this.controlId = controlId;
            this.fm = fm;
        }

        public String getControlId() {
            return controlId;
        }

        public FacesMessage getFm() {
            return fm;
        }
    }

    private List<MessageData> messages = new ArrayList<MessageData>();

    /**
     * Adds a raw message (without looking into Resource Bundle) for a given UI control base on controlId
     *
     * @param controlId UI control ID
     * @param message   a raw message to add
     * @param severity  FacesMessage's severity level
     */
    public void addMesageToControl(String controlId, String message, FacesMessage.Severity severity) {
        messages.add(new MessageData(controlId, new FacesMessage(severity, message, message)));
    }

    /**
     * Adds a raw message (without looking into Resource Bundle) as a global message
     *
     * @param message  a raw message to add
     * @param severity FacesMessage's severity level
     */
    public void addMesageToControl(String message, FacesMessage.Severity severity) {
        addMesageToControl(null, message, severity);
    }

    public void addEL(String elExpression, FacesMessage.Severity severity) {
        addELToControl(null, elExpression, severity);
    }

    public void addELToControl(String controlId, String elExpression, FacesMessage.Severity severity) {
        String message = elEvaluator.evaluate(elExpression, String.class);
        messages.add(new MessageData(controlId, new FacesMessage(severity, message, message)));
    }

    public void addInfoFromBundle(String key, Object... params) {
        addInfoFromBundleToControl(null, key, params);
    }

    public void addFromBundle(String key, FacesMessage.Severity severity, Object... params) {
        addFromBundleToControl(null, key, severity, params);
    }

    public void addInfoFromBundleToControl(String controlId, String key, Object... params) {
        addFromBundleToControl(controlId, key, FacesMessage.SEVERITY_INFO, params);
    }

    public void addFromBundleToControl(String controlId, String key, FacesMessage.Severity severity, Object... params) {
        String message = formatMessage(key, params);
        FacesMessage fm = new FacesMessage(severity, message, message);
        messages.add(new MessageData(controlId, fm));
    }

    public void beforeRenderResponse() {
        for (MessageData message : messages) {
            FacesContext.getCurrentInstance().addMessage(getClientId(message.getControlId()), message.getFm());
        }
        messages.clear();
    }

    public String formatMessage(String key, Object... params) {
        // TODO: multiple bundles?
        try {
            return tryFormatMessage(key, params);
        } catch (MissingResourceException e) {
            LOG.warn("Missing resource key: [" + key + "]", e);
            return "???" + key + "???";
        }
    }

    private String tryFormatMessage(String key, Object[] params) {
        ResourceBundle rb = ResourceBundle.getBundle("messages", currentLocale.getCurrentLocale());
        String value = rb.getString(key);
        return new MessageFormat(value).format(params);
    }

    private String getClientId(String id) {
        if (id == null) {
            return null;
        }
        FacesContext facesContext = FacesContext.getCurrentInstance();
        return getClientId(facesContext.getViewRoot(), id, facesContext);
    }

    private static String getClientId(UIComponent component, String id, FacesContext facesContext) {
        String componentId = component.getId();
        if (componentId != null && componentId.equals(id)) {
            return component.getClientId(facesContext);
        } else {
            Iterator iter = component.getFacetsAndChildren();
            while (iter.hasNext()) {
                UIComponent child = (UIComponent) iter.next();
                String clientId = getClientId(child, id, facesContext);
                if (clientId != null) return clientId;
            }
            return null;
        }
    }

}
