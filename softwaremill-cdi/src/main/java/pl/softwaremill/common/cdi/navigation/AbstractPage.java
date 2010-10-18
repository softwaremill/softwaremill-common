package pl.softwaremill.common.cdi.navigation;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewParameter;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewDeclarationLanguage;
import javax.faces.view.ViewMetadata;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public abstract class AbstractPage implements Page {
    private final Map<String, String> params;
    private final boolean requiresLogin;

    public AbstractPage(Map<String, String> params, boolean requiresLogin) {
        this.params = params;
        this.requiresLogin = requiresLogin;
    }

    protected abstract String computeViewId();

    public abstract Page copy(Map<String, String> params, boolean requiresLogin);

    public Page includeParam(String name, String value) {
        Map<String, String> newParams = new LinkedHashMap<String, String>(params);
        newParams.put(name, value);
        return copy(newParams, isRequiresLogin());
    }

    public Page redirect() {
        return includeParam("faces-redirect", "true");
    }

    public Page includeViewParams() {
        return includeParam("includeViewParams", "true");
    }

    public Page includeViewParam(String name) {
        String viewId = computeViewId();

        // Getting the metadata facet of the view
        FacesContext ctx = FacesContext.getCurrentInstance();
        ViewDeclarationLanguage vdl = ctx.getApplication().getViewHandler().getViewDeclarationLanguage(ctx, viewId);
        ViewMetadata viewMetadata = vdl.getViewMetadata(ctx, viewId);
        UIViewRoot viewRoot = viewMetadata.createMetadataView(ctx);
        UIComponent metadataFacet = viewRoot.getFacet(UIViewRoot.METADATA_FACET_NAME);

        // Looking for a view parameter with the specified name
        UIViewParameter viewParam = null;
        for (UIComponent child : metadataFacet.getChildren()) {
            if (child instanceof UIViewParameter) {
                UIViewParameter tempViewParam = (UIViewParameter) child;
                if (name.equals(tempViewParam.getName())) {
                    viewParam = tempViewParam;
                    break;
                }
            }
        }

        if (viewParam == null) {
            throw new FacesException("Unknown parameter: '" + name + "' for view: " + viewId);
        }

        // Getting the value
        String value = viewParam.getStringValue(ctx);
        return includeParam(name, value);
    }

    public String s() {
        StringBuilder sb = new StringBuilder();
        sb.append(computeViewId());

        String paramSeparator = "?";
        for (Map.Entry<String, String> nameValue : params.entrySet()) {
            sb.append(paramSeparator).append(nameValue.getKey()).append("=").append(nameValue.getValue());
            paramSeparator = "&amp;";
        }

        return sb.toString();
    }

    public String getS() { return s(); }

    public boolean isRequiresLogin() {
        return requiresLogin;
    }
}