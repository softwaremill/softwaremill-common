package pl.softwaremill.common.faces.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.softwaremill.common.cdi.el.ELEvaluator;
import pl.softwaremill.common.cdi.security.LoginBean;
import pl.softwaremill.common.cdi.util.BeanInject;
import pl.softwaremill.common.faces.navigation.NavBase;
import pl.softwaremill.common.faces.navigation.Page;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * TODO: go back after a redirect to login
 * <p/>
 * TODO: secure the cache:
 * //Forces caches to obtain a new copy of the page from the origin server
 * response.setHeader("Cache-Control","no-cache");
 * //Directs caches not to store the page under any circumstance
 * response.setHeader("Cache-Control","no-store");
 * //Causes the proxy cache to see the page as "stale"
 * response.setDateHeader("Expires", 0);
 * //HTTP 1.0 backward compatibility
 * response.setHeader("Pragma","no-cache");
 *
 * @author Adam Warski (adam at warski dot org)
 */
public class SecurityPhaseListener implements PhaseListener {

    private final static Logger LOG = LoggerFactory.getLogger(SecurityPhaseListener.class);

    private final static String PREVIOUS_VIEW = "previous:viewId:redirect";
    private final static String PREVIOUS_VIEW_PARAMETERS = "previous:viewId:parameters";

    private NavBase nav;

    public SecurityPhaseListener() {
        this.nav = BeanInject.lookup(NavBase.class);
    }

    public void beforePhase(PhaseEvent event) {
        FacesContext ctx = event.getFacesContext();
        HttpSession session = ((HttpSession) ctx.getExternalContext().getSession(true));
        LoginBean login = BeanInject.lookup(LoginBean.class);
        Page page = nav.lookup(event.getFacesContext().getViewRoot().getViewId());

        if (page.isRequiresLogin() && !login.isLoggedIn()) {
            if (nav.shouldRedirectToLogin()) {
                redirectToLogin(ctx, session, nav, page);
            } else {
                nav.responseForbidden(ctx);
            }
        } else if (login.isLoggedIn() && session.getAttribute(PREVIOUS_VIEW) != null) {
            redirectToPreviousView(ctx, session);
        } else if (page.getSecurityEL() != null && page.getSecurityEL().length() > 0) {
            evaluateSecurityExpression(ctx, page);
        }
    }

    private void redirectToLogin(FacesContext ctx, HttpSession session, NavBase nav, Page page) {
        session.setAttribute(PREVIOUS_VIEW, ctx.getApplication().getViewHandler().getActionURL(ctx, page.s()));
        LOG.debug("Stored PREVIOUS_VIEW = ["+ session.getAttribute(PREVIOUS_VIEW) + "] in session!");
        // this is needed as the default implementation returned by # getRequestParameterValuesMap() loose parameters when session is restored
        Map<String, List<String>> map = repackageParameters(ctx.getExternalContext().getRequestParameterValuesMap());
        session.setAttribute(PREVIOUS_VIEW_PARAMETERS, map);
        LOG.debug("Stored PREVIOUS_VIEW_PARAMETERS = ["+ session.getAttribute(PREVIOUS_VIEW_PARAMETERS) + "] in session!");
        try {
            String url = ctx.getApplication().getViewHandler().getActionURL(ctx, nav.getLogin().s());
            ctx.getExternalContext().redirect(ctx.getExternalContext().encodeActionURL(url));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ctx.responseComplete();
    }

    private Map<String, List<String>> repackageParameters(Map<String, String[]> requestParameterValuesMap) {
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        Set<Map.Entry<String, String[]>> entries = requestParameterValuesMap.entrySet();
        for (Map.Entry<String, String[]> entry : entries) {
            map.put(entry.getKey(), Arrays.asList(entry.getValue()));
        }
        return map;
    }

    private void redirectToPreviousView(FacesContext ctx, HttpSession session) {
        String redirect = (String) session.getAttribute(PREVIOUS_VIEW);
        Map parameters = (Map) session.getAttribute(PREVIOUS_VIEW_PARAMETERS);
        session.setAttribute(PREVIOUS_VIEW, null);
        session.setAttribute(PREVIOUS_VIEW_PARAMETERS, null);
        try {
            LOG.debug("Redirecting to [" + redirect +"] with params [" + parameters +"]");
            if (parameters != null && !parameters.isEmpty()) {
                ctx.getExternalContext().redirect(ctx.getExternalContext().encodeRedirectURL(redirect, parameters));
            } else {
                ctx.getExternalContext().redirect(ctx.getExternalContext().encodeActionURL(redirect));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ctx.responseComplete();
    }

    private void evaluateSecurityExpression(FacesContext ctx, Page page) {
        ELEvaluator evaluator = BeanInject.lookup(ELEvaluator.class);
        Boolean securityResult = evaluator.evaluate(page.getSecurityEL(), Boolean.class);
        if (securityResult == null) {
            throw new RuntimeException("Security EL: " + page.getSecurityEL() + " on page " + page.s() + " doesn't resolve to Boolean");
        }
        if (!securityResult) {
            nav.responseForbidden(ctx);
        }
    }

    public void afterPhase(PhaseEvent event) {
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RENDER_RESPONSE;
    }
}
