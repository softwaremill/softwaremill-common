package com.softwaremill.common.cdi.objectservice.extension;

import org.jboss.weld.util.reflection.HierarchyDiscovery;
import org.jboss.weld.util.reflection.ParameterizedTypeImpl;
import com.softwaremill.common.cdi.objectservice.OS;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.*;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class ObjectServiceExtension implements Serializable, Extension {
    /*
    An object service is a class which implements the OS interface, and its type parameter can be resolved
    to a class.

    Registration algorithm:
    1. Find annotated types which implement OS.
    2. Determine the exact type T of the type parameter. Proceed if it is a concrete class (not a wildcard, abstract
    class, etc.)
    3. Register a bean with type: OSP<T, OS<T>>, which provides the found service.

    Lookup algorithm:
    1. Given an object, search for the best object service specification (T, OS<T> pair)
    2. Create a new instance of a bean which implements the service
    3. Set the object using OS.setServiced()
    */

    // For some reason @Inject doesn't work, the field is assigned later
    private BeanManager beanManager;

    private Set<ObjectServiceSpecification> objectServicesSpecs = new HashSet<ObjectServiceSpecification>();
    private Set<ObjectServiceSpecification> objectServicesProviderSpecs = new HashSet<ObjectServiceSpecification>();

    private Type getObjectServiceTypeParameter(Iterable<Type> flattenedTypes) {
        for (Type flattenedType : flattenedTypes) {
            if (flattenedType instanceof ParameterizedType && ((ParameterizedType) flattenedType).getRawType().equals(OS.class)) {
                return ((ParameterizedType) flattenedType).getActualTypeArguments()[0];
            }
        }

        return null;
    }

    public Set<Type> getInterfaceClosure(Class<?> rawType) {
        Set<Type> types = new HashSet<Type>();
        for (Type t : rawType.getGenericInterfaces()) {
            types.addAll(new HierarchyDiscovery(t).getTypeClosure());
        }
        
        return types;
    }

    /**
     * Looks for the type parameter of the given annotated type, which has a raw type {@link com.softwaremill.common.cdi.objectservice.OS}.
     * @param at The class on which to look for the type parameter.
     * @return The type parameter or {@code null}, if no type parameter is specified.
     */
    private Type getObjectServiceTypeParameter(AnnotatedType<?> at) {
        Type typeParameter = getObjectServiceTypeParameter(getInterfaceClosure(at.getJavaClass()));
        if (typeParameter != null)  {
            return typeParameter;
        } else {
            return getObjectServiceTypeParameter(at.getTypeClosure());
        }
    }

    private Type[] createWildcards(int count) {
        Type[] ret = new Type[count];
        for (int i=0; i<count; i++) {
            ret[i] = PureWildcardType.INSTANCE;
        }

        return ret;
    }

    public <X> void processAnnotatedType(@Observes ProcessAnnotatedType<X> event) {        
        AnnotatedType<X> at = event.getAnnotatedType();

        // Checking if the class implements the OS interface
        if (OS.class.isAssignableFrom(at.getJavaClass())) {
            // Getting the type parameter type parameter for the OS implementation
            Type osTypeParameter = getObjectServiceTypeParameter(at);

            // If the type parameter is a type variable, searching for that type variable in the main class declaration.
            if (osTypeParameter instanceof TypeVariable) {
                String typeVariableName = ((TypeVariable) osTypeParameter).getName();
                for (TypeVariable<Class<X>> classTypeVariable : at.getJavaClass().getTypeParameters()) {
                    if (classTypeVariable.getName().equals(typeVariableName)) {
                        // Looking at the bound of the variable and if any, assigning it to the found type parameter
                        if (classTypeVariable.getBounds().length > 0) {
                            osTypeParameter = classTypeVariable.getBounds()[0];
                        }
                    }
                }
            }

            // Checking if the found type variable is a class. If so, registering a new object service.
            if (osTypeParameter instanceof Class) {
                Class<?> serviceClass = at.getJavaClass();

                // Checking if the class directly implements OS.
                boolean osFound = false;
                for (Class<?> serviceClassInterface : serviceClass.getInterfaces()) {
                    if (serviceClassInterface.isAssignableFrom(OS.class)) {
                        osFound = true;
                    }
                }

                // If the class has any type parameters, filling them in with wildcards
                Type serviceType;
                if (serviceClass.getTypeParameters().length > 0) {
                    serviceType = new ParameterizedTypeImpl(serviceClass, createWildcards(serviceClass.getTypeParameters().length), null);
                } else {
                    serviceType = serviceClass;
                }

                ObjectServiceSpecification osSpec = new ObjectServiceSpecification((Class<?>) osTypeParameter, serviceType, serviceClass);

                // Registering a new spec so that the bean can be found by the provider
                objectServicesSpecs.add(osSpec);

                if (osFound) {
                    // Registering a new OSP for class: osTypeParameter and service: serviceType
                    objectServicesProviderSpecs.add(osSpec);
                }
            }
        }
    }

    public void afterBeanDiscovery(@Observes AfterBeanDiscovery abd, BeanManager bm) {
        // Adding OSP beans according to the specifications gathered
        for (ObjectServiceSpecification objectServicesSpec : objectServicesProviderSpecs) {
            abd.addBean(new ObjectServiceProviderBean(this, objectServicesSpec));
        }

        // Storing the bean manager
        beanManager = bm;
    }

    /**
     * Finds a most specific service specification, given the class of the object and the class of the service.
     * @param objClass Class of the object.
     * @param serviceClass Class of the service.
     * @return A specification of a service for the same object class as the given one, with the most specific
     * service class.
     */
    private ObjectServiceSpecification serviceSpecForObject(Class<?> objClass, Class<?> serviceClass) {
        // TODO: cache results

        ObjectServiceSpecification bestSoFar = null;

        // Checking all registered specifications
        for (ObjectServiceSpecification candidateSpec : objectServicesSpecs) {
            // Checking if the checked spec is suitable for the requested one
            // Here we allow subclasses of objClass, to make sure that e.g. Hibernate Proxies work. However, this
            // will not work if there are object services for non-terminal nodes (non-leaves) in the inheritance tree.
            if (candidateSpec.getObjectClass().isAssignableFrom(objClass) &&
                    serviceClass.isAssignableFrom(candidateSpec.getServiceClass())) {
                // Checking if it's better (more specific) than the one currently found
                if (bestSoFar == null || bestSoFar.getServiceClass().isAssignableFrom(candidateSpec.getServiceClass())) {
                    bestSoFar = candidateSpec;
                }

                // TODO: check if there's no ambiguency (two "best")
            }
        }

        if (bestSoFar == null) {
            throw new RuntimeException("No " + serviceClass + " service found for object of class: " + objClass + ".");
        }

        return bestSoFar;
    }

    /**
     * For the given specification, finds an instance of a bean implementing the given service type. There must be
     * exactly one service matching the given type.
     * @param spec Specification of the service
     * @return An instance of a bean implementing the given service type.
     */
    private Object beanFromSpec(ObjectServiceSpecification spec) {
        Set<Bean<?>> bestServiceBeans = beanManager.getBeans(spec.getServiceType());
        if (bestServiceBeans.size() == 0) {
            throw new RuntimeException("No bean found for specification: " + spec);
        }

        if (bestServiceBeans.size() > 1) {
            throw new RuntimeException("Multiple beans (" + bestServiceBeans + ") found for specification:" + spec);
        }

        // Now we know there's only one
        Bean<?> bestServiceBean = bestServiceBeans.iterator().next();
        return beanManager.getReference(bestServiceBean,
                spec.getServiceType(),
                beanManager.createCreationalContext(bestServiceBean));
    }

    /**
     * @param objClass The class of the object
     * @param serviceClass The class of the service.
     * @return An instance of a bean, which is the most specific implementation of an object service for the given
     * object class and service class.
     */
    public Object serviceForObject(Class<?> objClass, Class<?> serviceClass) {
        ObjectServiceSpecification bestSoFar = serviceSpecForObject(objClass, serviceClass);
        return beanFromSpec(bestSoFar);
    }
}
