package pl.softwaremill.common.cdi.navigation;

import javax.faces.context.FacesContext;
import java.util.Collections;
import java.util.Map;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class CurrentPage extends AbstractPage {
    private String viewId;

    public CurrentPage() {
        this(null, Collections.<String, String>emptyMap());
    }

    public CurrentPage(String viewId, Map<String, String> params) {
        super(params, false);

        this.viewId = viewId;
    }

    @Override
    protected String computeViewId() {
        if (viewId == null) {
            viewId = FacesContext.getCurrentInstance().getViewRoot().getViewId();
        }

        return viewId;
    }

    public Page copy(Map<String, String> params, boolean requiresLogin) {
        return new CurrentPage(viewId, params);
    }

    @Override
    public String getSecurityEL() {
        return null;
    }
}
