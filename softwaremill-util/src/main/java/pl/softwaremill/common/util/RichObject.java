package pl.softwaremill.common.util;

import java.lang.reflect.Field;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class RichObject {
    private final Object wrapped;

    public RichObject(Object wrapped) {
        this.wrapped = wrapped;
    }

    public void set(String propertyName, Object value) {
        try {
            Field f = wrapped.getClass().getDeclaredField(propertyName);
            f.setAccessible(true);
            f.set(wrapped, value);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Object get(String propertyName) {
        try {
            Field f = wrapped.getClass().getDeclaredField(propertyName);
            f.setAccessible(true);
            return f.get(wrapped);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}

