package com.softwaremill.common.cdi.objectservice;

/**
 * A marker interface for services for particular type of objects.
 * @author Adam Warski (adam at warski dot org)
 */
public interface OS<T> {
    /**
     * Sets the object, that is being serviced.
     * @param serviced The serviced object.
     */
    void setServiced(T serviced);
}
