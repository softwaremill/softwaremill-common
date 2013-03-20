package com.softwaremill.common.faces.navigation;

import javax.faces.context.FacesContext;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public abstract class NavBase {
    private Map<String, Page> pagesByViewId = new HashMap<String, Page>();

    public void register(String viewId, Page page) {
        pagesByViewId.put(viewId, page);
    }

    public Page lookup(String viewId) {
        Page p = pagesByViewId.get(viewId);
        if (p == null) {
            throw new RuntimeException("Page with view id " + viewId + " not found.");
        }
        return p;
    }

    protected class ViewIdPageBuilder {
        private final String viewId;

        private boolean requiresLogin;

        private String securityEL;

        public ViewIdPageBuilder(String viewId) {
            this.viewId = viewId;
            requiresLogin = false;
        }

        public ViewIdPageBuilder setRequiresLogin(boolean requiresLogin) {
            this.requiresLogin = requiresLogin;
            return this;
        }

        public ViewIdPageBuilder setSecurityEL(String securityEL) {
            this.securityEL = securityEL;
            return this;
        }

        public Page b() {
            Page p = new ViewIdPage(viewId, new LinkedHashMap<String, String>(), requiresLogin, securityEL);
            register(viewId, p);
            return p;
        }
    }

    private final Page currentPage = new CurrentPage();

    public Page getCurrentPage() {
        return currentPage;
    }

    public abstract Page getLogin();

    public abstract Page getError();

	/**
	 * Method executed when the security el expression for the given page returns false
	 * @param ctx {@code FacesContext}
	 */
	public void responseForbidden(FacesContext ctx) {
		ctx.getExternalContext().setResponseStatus(403);
		ctx.responseComplete();
	}

    /**
     * If this returns true, the user will be redirected to a login page once trying to access restricted page.
     * <p/>
     * Default is true.
     *
     * @return Treu or false - default is true
     */
    public boolean shouldRedirectToLogin() {
        return true;
    }
}
