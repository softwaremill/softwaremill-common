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

    public void beforePhase(PhaseEvent event) {
        // check if we don't need to redirect
        FacesContext ctx = event.getFacesContext();
        HttpSession session = ((HttpSession) ctx.getExternalContext().getSession(true));

        NavBase nav = BeanInject.lookup(NavBase.class);
        LoginBean login = BeanInject.lookup(LoginBean.class);

        Page page = nav.lookup(event.getFacesContext().getViewRoot().getViewId());

        // if needed - handle redirects
        if (nav.shouldRedirectToLogin()) {
            // check redirect only if we're not on the login page
            if (!page.equals(nav.getLogin()) &&
                    // and redirect exists
                    session.getAttribute(PREVIOUS_VIEW) != null) {

                String redirect = (String) session.getAttribute(PREVIOUS_VIEW);

                // set null so it doesn't re-redirect
                session.setAttribute(PREVIOUS_VIEW, null);

                // finally perform the redirect
                try {
                    ctx.getExternalContext().redirect(ctx.getExternalContext().encodeActionURL(redirect));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                ctx.responseComplete();
                return;
            }

            if (page.isRequiresLogin() && !login.isLoggedIn()) {
                String url = ctx.getApplication().getViewHandler().getActionURL(ctx, nav.getLogin().s());

                // remember this view to redirect back to it
                session.setAttribute(PREVIOUS_VIEW, ctx.getApplication().getViewHandler().getActionURL(ctx, page.s()));

                try {

                    ctx.getExternalContext().redirect(ctx.getExternalContext().encodeActionURL(url));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                ctx.responseComplete();

                return;
            }
        }
        else {
            // otherwise check isLoggerIn()
            if (page.isRequiresLogin() && !login.isLoggedIn()) {
                // show 403 Forbidden - user not logged in and do not know what to do with it
                ctx.getExternalContext().setResponseStatus(403);

                ctx.responseComplete();
            }
        }


        // otherwise check the security el

        if (page.getSecurityEL() != null && page.getSecurityEL().length() > 0) {
            ELEvaluator evaluator = BeanInject.lookup(ELEvaluator.class);

            Boolean securityResult = evaluator.evaluate(page.getSecurityEL(), Boolean.class);

            if (securityResult == null) {
                throw new RuntimeException("Security EL: " + page.getSecurityEL() +
                        "on page " + page.s() + " doesn't resolve to Boolean");
            }

            if (!securityResult) {
                // show 403 Forbidden - the security EL resolved to false
                ctx.getExternalContext().setResponseStatus(403);

                ctx.responseComplete();
            }
        }
    }

    public void afterPhase(PhaseEvent event) {
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RENDER_RESPONSE;
    }
}
