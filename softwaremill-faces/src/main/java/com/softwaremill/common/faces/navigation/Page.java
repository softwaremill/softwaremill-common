package com.softwaremill.common.faces.navigation;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public interface Page {
    Page redirect();
    Page includeViewParams();
    Page includeViewParam(String name);
    Page includeParam(String name, String value);

    String s();
    String getS();
    
    boolean isRequiresLogin();
    String getSecurityEL();
}