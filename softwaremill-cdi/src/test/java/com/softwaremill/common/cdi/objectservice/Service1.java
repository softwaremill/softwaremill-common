package com.softwaremill.common.cdi.objectservice;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public interface Service1<T extends A> extends OS<T> {
    Object get();
}
