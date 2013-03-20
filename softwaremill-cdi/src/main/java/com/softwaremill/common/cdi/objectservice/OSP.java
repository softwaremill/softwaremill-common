package pl.softwaremill.common.cdi.objectservice;

import java.io.Serializable;

/**
 * An object service provider.
 * @author Adam Warski (adam at warski dot org)
 */
public interface OSP<O, S extends OS<O>> extends Serializable {
    /**
     * Finds the appropriate object service for the given object, that is, finds a bean implementing the service
     * interface (as specified in the type parameter {@link S}), for which the type parameter corresponds to the
     * class of the bean passed.
     *
     * A new instance of an object service is created on each invocation.
     *
     * @param o The object for which to get the object service.
     * @return An object service for the given object.
     */
    S f(O o);
}
