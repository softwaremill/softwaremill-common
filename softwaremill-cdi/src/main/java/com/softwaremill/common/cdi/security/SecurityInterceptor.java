package com.softwaremill.common.cdi.security;

import com.google.common.collect.ImmutableMap;
import com.softwaremill.common.cdi.el.ELEvaluator;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Adam Warski (adam at warski dot org)
 */
@Interceptor
@InterceptSecure({})
public class SecurityInterceptor implements Serializable {
    @Inject
    private SecurityExtension se;

    @Inject
    private ELEvaluator elEvaluator;

    @Inject
    private SecurityFlags securityFlags;

    @AroundInvoke
    public Object invoke(InvocationContext ctx) throws Exception {
        // Getting the generated @InterceptSecure annotation for the method.
        // After the CDI Maintenance Release is released, it should be possible to get the annotated type of the
        // currently invoked bean, see:
        // http://old.nabble.com/Retrieving-the-Bean-object-for-an-interceptor-td28147499.html
        // For now, we just use a map in the extension. One limitation is that this doesn't allow different security
        // annotations for methods which are used in several beans.
        InterceptSecure is = se.getInterceptSecure(ctx.getMethod());

        // First checking the security flags
        AllowWithFlag[] allowWithFlags = is == null? new AllowWithFlag[0] : is.flags();
        for (AllowWithFlag allowWithFlag : allowWithFlags) {
            for (String flag : allowWithFlag.value()) {
                if (securityFlags.hasFlag(flag)) {
                    return ctx.proceed();
                }
            }
        }

        // Then checking the restrictions
        Secure[] constraints = is == null ? new Secure[0] : is.value();

        // Getting the parameter values
        // TODO: compute at the beginning
        Map<String, Object> secureVars = computeParameterValues(ctx);

        for (Secure constraint : constraints) {
            Boolean expressionValue = elEvaluator.evaluate(constraint.value(), Boolean.class, secureVars);

            if (expressionValue == null || !expressionValue) {
                // TODO: message
                throw new SecurityConditionException();
            }
        }


        return ctx.proceed();
    }

    private Map<String, Object> computeParameterValues(InvocationContext ctx) {
        Annotation[][] parametersAnnotations = ctx.getMethod().getParameterAnnotations();
        Map<String, Object> secureVars = new HashMap<String, Object>();
        for (int i = 0; i < parametersAnnotations.length; i++) {
            Annotation[] parameterAnnotations = parametersAnnotations[i];
            for (Annotation parameterAnnotation : parameterAnnotations) {
                if (SecureVar.class.isAssignableFrom(parameterAnnotation.annotationType())) {
                    SecureVar secureVar = (SecureVar) parameterAnnotation;
                    Object paramValue = ctx.getParameters()[i];
                    // If there's an expression associated with the variable, use its value instead of the
                    // parameter.
                    if (!"".equals(secureVar.exp())) {
                        paramValue = evaluateSecureVarExp(paramValue, secureVar);
                    }
                    secureVars.put(secureVar.value(), paramValue);
                }
            }
        }

        return secureVars;
    }

    public Object evaluateSecureVarExp(Object base, SecureVar secureVar) {
        return elEvaluator.evaluate(secureVar.exp(), Object.class, ImmutableMap.of("p", base));
    }
}
