package com.softwaremill.common.cdi.autofactory.extension;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;

import static com.google.common.collect.Maps.*;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class MethodParameterIndexer {
    private final Method factoryMethod;
    private final Map<Type, Integer> parameterTypeToIndex;

    public MethodParameterIndexer(Method factoryMethod) {
        this.factoryMethod = factoryMethod;

        parameterTypeToIndex = newHashMap();

        Type[] parameterTypes = factoryMethod.getGenericParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            Type parameterType = parameterTypes[i];
            parameterTypeToIndex.put(parameterType, i);
        }
    }

    public int getIndexForArgument(Type parameterType) {
        if (!parameterTypeToIndex.containsKey(parameterType)) {
            throw new RuntimeException("No parameter of class: " + parameterType + " in the factory method: " + factoryMethod);
        }

        return parameterTypeToIndex.get(parameterType);
    }
}
