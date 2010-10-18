package pl.softwaremill.common.faces.i18n;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.Cookie;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Adam Warski (adam at warski dot org)
 */
@SessionScoped
public class CurrentLocale implements Serializable {
    private final static String LOCALE_COOKIE_NAME = "choosen-locale";

    private volatile Locale currentLocale;

    public Locale getCurrentLocale() {
        if (currentLocale == null) {
            synchronized (this) {
                if (currentLocale == null) {
                    // Trying to read from the cookie
                    Object localeCookie = FacesContext.getCurrentInstance().getExternalContext().getRequestCookieMap()
                            .get(LOCALE_COOKIE_NAME);
                    String localeCookieValue = null;
                    if (localeCookie != null && (localeCookie instanceof Cookie)) {
                        localeCookieValue = ((Cookie) localeCookie).getValue();
                    }

                    if (localeCookieValue != null) {
                        currentLocale = new Locale(localeCookieValue);
                    }

                    // TODO: read from the domain
                }
            }
        }

        return currentLocale;
    }

    public void setCurrentLocale(Locale currentLocale) {
        this.currentLocale = currentLocale;

        // Setting the client-side cookie
        Map<String, Object> cookieProperties = new HashMap<String, Object>();
        cookieProperties.put("maxAge", 60 * 60 * 24 * 30); // 1 month
        FacesContext.getCurrentInstance().getExternalContext().addResponseCookie(
                LOCALE_COOKIE_NAME, currentLocale.toString(), cookieProperties);
    }
}
