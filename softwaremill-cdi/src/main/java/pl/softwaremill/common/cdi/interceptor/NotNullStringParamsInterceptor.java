package pl.softwaremill.common.cdi.interceptor;

import com.google.common.base.Strings;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

/**
 * @author Pawel Wrzeszcz (pawel [at] softwaremill . com)
 */
@NullToEmptyStringParams
@Interceptor
public class NotNullStringParamsInterceptor {
	@AroundInvoke
	public Object convertEmptyStringParameters(InvocationContext ctx) throws Exception {

		Object[] parameters = ctx.getParameters();
		Object[] convertedParameters = new Object[parameters.length];
		
		int i = 0;
		for (Class<?> parameterType : ctx.getMethod().getParameterTypes()) {
			convertedParameters[i] = convertParamValue(parameterType, parameters[i]);
			i++;
		}

		ctx.setParameters(convertedParameters);
		
		return ctx.proceed();
	}

	private Object convertParamValue(Class<?> parameterType, Object parameter) {
		return parameterType.equals(String.class) ? Strings.nullToEmpty((String) parameter) : parameter;
	}
}
