package com.softwaremill.common.cdi.security;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class SecureBeanStacked {
    public void method1() { }

    @NoAccess
    public void method2() { }
}