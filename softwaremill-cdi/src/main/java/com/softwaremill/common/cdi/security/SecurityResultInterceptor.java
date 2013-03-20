package pl.softwaremill.common.cdi.security;

import com.google.common.collect.ImmutableMap;
import pl.softwaremill.common.cdi.el.ELEvaluator;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.io.Serializable;

/**
 * @author Adam Warski (adam at warski dot org)
 */
@Interceptor
@SecureResult("")
public class SecurityResultInterceptor implements Serializable {
    @Inject
    private ELEvaluator elEvaluator;

    @AroundInvoke
    public Object invoke(InvocationContext ctx) throws Exception {

        // Getting the result
        Object result = ctx.proceed();

        // And checking the condition
        SecureResult sr = ctx.getMethod().getAnnotation(SecureResult.class);
        Boolean expressionValue = evaluateSecureResultExp(result, sr);

        if (expressionValue == null || !expressionValue) {
            // TODO: message
            throw new SecurityConditionException();
        }

        return result;
    }

    public Boolean evaluateSecureResultExp(Object base, SecureResult secureResult) {
        return elEvaluator.evaluate(secureResult.value(), Boolean.class, ImmutableMap.of("result", base));
    }
}