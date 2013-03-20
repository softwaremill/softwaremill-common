package pl.softwaremill.common.cdi.objectservice;

/**
 * A service for which implementations inherit from an abtract service.
 * @author Adam Warski (adam at warski dot org)
 */
public interface Service3<T extends A> extends OS<T> {
    Object get();
}
