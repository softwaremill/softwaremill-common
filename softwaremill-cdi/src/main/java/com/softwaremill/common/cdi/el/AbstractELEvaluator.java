package pl.softwaremill.common.cdi.el;

import java.util.Map;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public abstract class AbstractELEvaluator implements ELEvaluator {
    public abstract <T> T evaluate(String expression, Class<T> expectedResultType);

    @SuppressWarnings({"unchecked"})
    public <T> T evaluate(String expression, Class<T> expectedResultType, Map<String, Object> parameters) {
        try {
            // Setting parameters
            for (Map.Entry<String, Object> parameter : parameters.entrySet()) {
                setParameter(parameter.getKey(), parameter.getValue());
            }

            // Evaluating expression
            return evaluate(expression, expectedResultType);
        } finally {
            // Clearing up parameters
            for (String parameterName : parameters.keySet()) {
                clearParameter(parameterName);
            }
        }
    }

    public abstract void setParameter(String name, Object value);

    public abstract void clearParameter(String name);
}
