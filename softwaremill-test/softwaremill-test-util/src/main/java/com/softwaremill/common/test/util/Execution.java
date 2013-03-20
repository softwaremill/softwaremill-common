package com.softwaremill.common.test.util;

/**
 * Used in tests to get exception thrown by the closure provided in the <code>execute</code> method.
 *
 * @author Pawel Wrzeszcz (pawel . wrzeszcz [at] gmail . com)
 */
public abstract class Execution {

	private Exception e;

	protected abstract void execute() throws Exception;

	public Exception getException() {
		if (e == null) {
			try {
				execute();
			} catch (Exception ex) {
				e = ex;
			}
		}

		return e;
	}
}
