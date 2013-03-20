package com.softwaremill.common.cdi.interceptor;

/**
 * @author Pawel Wrzeszcz (pawel [at] softwaremill . com)
 */
public class BeanToIntercept {

	@NullToEmptyStringParams
	public String[] method(String param1, String param2) {
		return new String[] {param1, param2};
	}

	@NullToEmptyStringParams
	public Object[] method2(Object param1, String param2) {
		return new Object[] {param1, param2};
	}
}
