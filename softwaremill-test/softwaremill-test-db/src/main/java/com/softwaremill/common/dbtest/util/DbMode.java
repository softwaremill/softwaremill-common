package com.softwaremill.common.dbtest.util;

/**
 * Created by Pawel Stawicki on Mar 31, 2011 9:38:46 PM
 */
public enum DbMode {

    DB2("DB2"), Derby("Derby"), HSQLDB("HSQLDB"), MSSQL("MSSQLServer"),
    MySQL("MySQL"), Oracle("Oracle"), PostgreSQL("PostgreSQL");

    private DbMode(String compatibilityParamValue) {
        this.compatibilityParamValue = compatibilityParamValue;
    }

    private String compatibilityParamValue;

    public String getParameterValue() {
        return compatibilityParamValue;
    }
}
