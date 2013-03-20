package com.softwaremill.common.cdi.security;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class SecureBeanSecureVarExp {
    @Secure("#{var1 == 'a'}")
    public void method1(@SecureVar(value = "var1", exp = "#{p.content}") StringHolder sh) { }
}