package pl.softwaremill.common.cdi.security;

/**
 * Should be implemented by the bean which controls the logged-in status of the user.
 * @author Adam Warski (adam at warski dot org)
 */
public interface LoginBean {
    /**
     * @return True iff a user is currently logged in.
     */
    boolean isLoggedIn();
}
