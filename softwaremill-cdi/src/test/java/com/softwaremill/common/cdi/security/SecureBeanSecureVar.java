package pl.softwaremill.common.cdi.security;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class SecureBeanSecureVar {
    @Secure("#{var1 == true}")
    public void method1(@SecureVar("var1") boolean var1) { }
}