package com.softwaremill.common.cdi.security;

import org.jboss.solder.reflection.annotated.AnnotatedTypeBuilder;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.util.AnnotationLiteral;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class SecurityExtension implements Extension, Serializable {
    private final Map<Method, InterceptSecure> interceptSecureForMethods = new HashMap<Method, InterceptSecure>();

    public InterceptSecure getInterceptSecure(Method m) {
        return interceptSecureForMethods.get(m);
    }

    public <T> void processAnnotatedType(@Observes ProcessAnnotatedType<T> event) {
        // A flag indicating if the builder was used to modify the annotations
        boolean used = false;
        AnnotatedTypeBuilder<T> builder = new AnnotatedTypeBuilder<T>().readFromType(event.getAnnotatedType());

        // We need to read the values of the @Secure annotation that are present on:
        // 1. types (classes)
        // 2. methods
        // 3. arbitrarily nested on @SecureBinding annotations

        // Gathering the initial secure values from the type
        // TODO: look in superclasses?
        List<Secure> initialSecureAnnotations = new ArrayList<Secure>();
        List<AllowWithFlag> initialAllowWithFlagsAnnotations = new ArrayList<AllowWithFlag>();
        for (Annotation annotation : event.getAnnotatedType().getAnnotations()) {
            collectAnnotations(Secure.class, annotation, initialSecureAnnotations);
            collectAnnotations(AllowWithFlag.class, annotation, initialAllowWithFlagsAnnotations);
        }

        for (AnnotatedMethod<?> m : event.getAnnotatedType().getMethods()) {
            // Gathering the secure annotations from the method
            final List<Secure> annotations = new ArrayList<Secure>(initialSecureAnnotations);
            final List<AllowWithFlag> allowWithFlagsAnnotations = new ArrayList<AllowWithFlag>(initialAllowWithFlagsAnnotations);

            collectAnnotations(Secure.class, m, annotations);
            collectAnnotations(AllowWithFlag.class, m, allowWithFlagsAnnotations);

            // If any annotations have been gathered, adding the annotation to the method and storing it
            // in the map.
            if (annotations.size() > 0) {
                InterceptSecure is = new InterceptSecureImpl(
                        annotations.toArray(new Secure[annotations.size()]),
                        allowWithFlagsAnnotations.toArray(new AllowWithFlag[allowWithFlagsAnnotations.size()]));
                
                builder.addToMethod(m.getJavaMember(), is);
                used = true;

                interceptSecureForMethods.put(m.getJavaMember(), is);
            }
        }

        // Setting the new annotated type, if any changes were made
        if (used) {
            event.setAnnotatedType(builder.create());
        }
    }

    private <T> void collectAnnotations(Class<T> annotationType, AnnotatedMethod m, List<T> values) {
        for (Annotation annotation : m.getAnnotations()) {
            collectAnnotations(annotationType, annotation, values);
        }
    }

    private <T> void collectAnnotations(Class<T> annotationType, Annotation annotation, List<T> values) {
        if (annotationType.isAssignableFrom(annotation.annotationType())) {
            //noinspection unchecked
            values.add((T) annotation);
        } else {
            if (annotation.annotationType().getAnnotation(SecureBinding.class) != null) {
                for (Annotation nestedAnnotation : annotation.annotationType().getAnnotations()) {
                    collectAnnotations(annotationType, nestedAnnotation, values);
                }
            }
        }
    }

    private static class InterceptSecureImpl extends AnnotationLiteral<InterceptSecure> implements InterceptSecure {
        private final Secure[] values;
        private final AllowWithFlag[] flags;

        private InterceptSecureImpl(Secure[] values, AllowWithFlag[] flags) {
            this.values = values;
            this.flags = flags;
        }

        @Override
        public Secure[] value() {
            return values;
        }

        @Override
        public AllowWithFlag[] flags() {
            return flags;
        }
    }
}
