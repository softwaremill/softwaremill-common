package com.softwaremill.common.cdi.security;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class SecureResultBean {
    @SecureResult("#{result == 'a'}")
    public String method1(String p) { return p; }
}