package pl.softwaremill.common.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class CDIInjector<T> {
    private final T target;

    private CDIInjector(T target) {
        this.target = target;
    }

    public CDIInjector<T> inject(Object... objects) {
        for (Object obj : objects) {
            doInject(null, obj);
        }

        return this;
    }

    public CDIInjector<T> injectWithQualifier(Class<? extends Annotation> qualifier, Object obj) {
        doInject(qualifier, obj);
        return this;
    }

    private void doInject(Class<? extends Annotation> qualifier, Object obj) {
        for (Field field : target.getClass().getDeclaredFields()) {
            if (fieldTypeAssignableFrom(field, obj.getClass()) && fieldHasQualifier(field, qualifier)) {
                checkFieldNotYetAssigned(field);
                new RichObject(target).set(field.getName(), obj);
                return;
            }
        }

        throw new IllegalStateException("Cannot inject " + obj + ": no sutiable field found in " + target);
    }

    private void checkFieldNotYetAssigned(Field field) {
        Object currentValue = new RichObject(target).get(field.getName());
        if (currentValue != null) {
            throw new IllegalStateException("Field " + field.getName() + " in object " + target +
                    " already has a value: " + currentValue);
        }
    }

    private boolean fieldHasQualifier(Field field, Class<? extends Annotation> qualifier) {
        return qualifier == null || field.getAnnotation(qualifier) != null;
    }

    private boolean fieldTypeAssignableFrom(Field field, Class<?> cls) {
        return field.getType().isAssignableFrom(cls);
    }

    public static <T> CDIInjector<T> into(T target) {
        return new CDIInjector<T>(target);
    }
}
