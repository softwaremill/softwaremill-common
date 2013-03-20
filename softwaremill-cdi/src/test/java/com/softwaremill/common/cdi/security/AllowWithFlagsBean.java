package com.softwaremill.common.cdi.security;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class AllowWithFlagsBean {
    @AllowWithFlag("flag1")
    @Secure("#{false}")
    public void testSingleFlagOnly() { }

    @AllowWithFlag({ "flag1", "flag2" })
    @Secure("#{false}")
    public void testDoubleFlagOnly() { }

    @AllowWithFlag("flag1")
    @Secure("#{var1}")
    public void testFlagWithSecure(@SecureVar("var1") boolean var1) { }
}
