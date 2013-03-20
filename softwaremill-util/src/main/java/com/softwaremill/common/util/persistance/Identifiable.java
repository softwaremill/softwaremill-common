package pl.softwaremill.common.util.persistance;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public interface Identifiable<T> {
    T getId();
    void setId(T id);
}
