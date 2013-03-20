package com.softwaremill.common.cdi.el;

import javax.el.ELContext;
import javax.el.ExpressionFactory;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class ELEvaluatorUtil {
    @SuppressWarnings({"unchecked"})
    public static <T> T evaluate(ELContext elContext, ExpressionFactory expressionFactory, String expression,
                                 Class<T> expectedResultType) {
        return (T) expressionFactory
                    .createValueExpression(elContext, expression, expectedResultType)
                    .getValue(elContext);
    }
}
