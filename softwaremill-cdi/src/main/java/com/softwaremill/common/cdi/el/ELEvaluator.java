package com.softwaremill.common.cdi.el;

import java.util.Map;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public interface ELEvaluator {
    <T> T evaluate(String expression, Class<T> expectedResultType);

    <T> T evaluate(String expression, Class<T> expectedResultType, Map<String, Object> parameters);
}
