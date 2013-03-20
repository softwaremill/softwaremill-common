package pl.softwaremill.common.faces.navigation;

import javax.faces.context.FacesContext;
import java.util.Collections;
import java.util.Map;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class CurrentPage extends AbstractPage {

    public CurrentPage() {
        super(Collections.<String, String>emptyMap(), false);
    }

    public CurrentPage(Map<String, String> params, boolean requiresLogin) {
        super(params, requiresLogin);
    }

    @Override
    protected String computeViewId() {
        return FacesContext.getCurrentInstance().getViewRoot().getViewId();
    }

    public Page copy(Map<String, String> params, boolean requiresLogin) {
        return new CurrentPage(params, requiresLogin);
    }

    @Override
    public String getSecurityEL() {
        return null;
    }
}
