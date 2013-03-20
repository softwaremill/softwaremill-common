package com.softwaremill.common.cdi.config;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.InjectionException;
import javax.enterprise.inject.spi.*;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author Gavin King
 * @author Adam Warski (adam at warski dot org)
 * @link http://in.relation.to/13053.lace
 */
public class ConfigExtension implements Extension {
    public <X> void processInjectionTarget(@Observes ProcessInjectionTarget<X> pit) {
        final InjectionTarget<X> it = pit.getInjectionTarget();
        final Map<Field, Object> configuredValues = new HashMap<Field, Object>();

        AnnotatedType<X> at = pit.getAnnotatedType();
        Annotation annotation = at.getAnnotation(Configuration.class);
        if(annotation == null){
            //don't process classes not marked with @Configuration annotation
            return;
        }
        String propsFileName = at.getJavaClass().getSimpleName() + ".properties";
        InputStream stream = at.getJavaClass().getResourceAsStream(propsFileName);
        if (stream!=null) {
            try {
                Properties props = new Properties();
                props.load(stream);
                for (Map.Entry<Object, Object> property : props.entrySet()) {
                    String fieldName = property.getKey().toString();
                    Object value = property.getValue();
                    try {
                        Field field = at.getJavaClass().getDeclaredField(fieldName);
                        field.setAccessible(true);
                        if ( field.getType().isAssignableFrom( value.getClass() ) ) {
                            configuredValues.put(field, value);
                        }
                        else {
                            //TODO: do type conversion automatically
                            pit.addDefinitionError( new InjectionException("field is not of type String: " + field ) );
                        }
                    }
                    catch (NoSuchFieldException nsfe) {
                        pit.addDefinitionError(nsfe);
                    }
                }
            }
            catch (IOException ioe) {
                pit.addDefinitionError(ioe);
            }
            finally {
                try {
                    stream.close();
                } catch (IOException e) {
                    pit.addDefinitionError(e);
                }
            }

            InjectionTarget<X> wrapped = new InjectionTarget<X>() {

                @Override
                public void inject(X instance, CreationalContext<X> ctx) {
                    it.inject(instance, ctx);
                    for (Map.Entry<Field, Object> configuredValue: configuredValues.entrySet()) {
                        try {
                            configuredValue.getKey().set(instance, configuredValue.getValue());
                        }
                        catch (Exception e) {
                            throw new InjectionException(e);
                        }
                    }
                }

                @Override
                public void postConstruct(X instance) {
                    it.postConstruct(instance);
                }

                @Override
                public void preDestroy(X instance) {
                    it.dispose(instance);
                }

                @Override
                public void dispose(X instance) {
                    it.dispose(instance);
                }

                @Override
                public Set<InjectionPoint> getInjectionPoints() {
                    return it.getInjectionPoints();
                }

                @Override
                public X produce(CreationalContext<X> ctx) {
                    return it.produce(ctx);
                }

            };

            pit.setInjectionTarget(wrapped);
        }
    }
}
