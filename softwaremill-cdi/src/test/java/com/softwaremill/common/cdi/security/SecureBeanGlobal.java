package pl.softwaremill.common.cdi.security;

/**
 * @author Adam Warski (adam at warski dot org)
 */
@Secure("#{false}")
public class SecureBeanGlobal {
    public void method1() { }
}