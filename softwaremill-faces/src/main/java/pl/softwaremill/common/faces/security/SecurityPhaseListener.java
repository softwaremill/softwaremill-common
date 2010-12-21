package pl.softwaremill.common.faces.security;

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

    private final static String PREVIOUS_VIEW = "previous:viewId:redirect";

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
                responseForbidden(ctx);
            }
        } else if (login.isLoggedIn() && session.getAttribute(PREVIOUS_VIEW) != null) {
            redirectToPreviousView(ctx, session);
        } else if (page.getSecurityEL() != null && page.getSecurityEL().length() > 0) {
            evaluateSecurityExpression(ctx, page);
        }
    }

    private void redirectToLogin(FacesContext ctx, HttpSession session, NavBase nav, Page page) {
        session.setAttribute(PREVIOUS_VIEW, ctx.getApplication().getViewHandler().getActionURL(ctx, page.s()));
        try {
            String url = ctx.getApplication().getViewHandler().getActionURL(ctx, nav.getLogin().s());
            ctx.getExternalContext().redirect(ctx.getExternalContext().encodeActionURL(url));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ctx.responseComplete();
    }

    private void redirectToPreviousView(FacesContext ctx, HttpSession session) {
        String redirect = (String) session.getAttribute(PREVIOUS_VIEW);
        session.setAttribute(PREVIOUS_VIEW, null);
        try {
            ctx.getExternalContext().redirect(ctx.getExternalContext().encodeActionURL(redirect));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ctx.responseComplete();
    }

    private void responseForbidden(FacesContext ctx) {
        ctx.getExternalContext().setResponseStatus(403);
        ctx.responseComplete();
    }

    private void evaluateSecurityExpression(FacesContext ctx, Page page) {
        ELEvaluator evaluator = BeanInject.lookup(ELEvaluator.class);
        Boolean securityResult = evaluator.evaluate(page.getSecurityEL(), Boolean.class);
        if (securityResult == null) {
            throw new RuntimeException("Security EL: " + page.getSecurityEL() + "on page " + page.s() + " doesn't resolve to Boolean");
        }
        if (!securityResult) {
            responseForbidden(ctx);
        }
    }

    public void afterPhase(PhaseEvent event) {
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RENDER_RESPONSE;
    }
}
