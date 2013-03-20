package pl.softwaremill.common.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import org.reflections.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class RichObject {
    private final Object wrapped;

    public RichObject(Object wrapped) {
        this.wrapped = wrapped;
    }

    public RichObject set(String propertyName, Object value) {
        try {
            Field f = getFieldWithName(propertyName);
            f.setAccessible(true);
            f.set(wrapped, value);
            return this;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     You should use set(String propertyName, Object value) which now uses all fields from the class hierarchy
     */
    @Deprecated
    public RichObject setSuper(String propertyName, Object value) {
        try {
            Field f = wrapped.getClass().getSuperclass().getDeclaredField(propertyName);
            f.setAccessible(true);
            f.set(wrapped, value);
            return this;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Object get(String propertyName) {
        try {
            Field f = getFieldWithName(propertyName);
            f.setAccessible(true);
            return f.get(wrapped);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    private Field getFieldWithName(String propertyName) {
        Set<Field> fields = ReflectionUtils.getAllFields(wrapped.getClass(), ReflectionUtils.withName(propertyName));
        Preconditions.checkState(fields.size() == 1, "There are %s with name '%s' in this class hierarchy!", fields.size(), propertyName);

        return Iterables.getFirst(fields, null);
    }

    /**
        You should use get(String propertyName) which now uses all fields from the class hierarchy
     */
    @Deprecated
    public Object getSuper(String propertyName) {
        try {
            Field f = wrapped.getClass().getSuperclass().getDeclaredField(propertyName);
            f.setAccessible(true);
            return f.get(wrapped);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Object unwrap() {
        return wrapped;
    }
}

