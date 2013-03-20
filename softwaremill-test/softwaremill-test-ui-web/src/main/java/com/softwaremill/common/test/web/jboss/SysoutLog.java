package com.softwaremill.common.test.web.jboss;

/**
 * @author Pawel Wrzeszcz (pawel [at] softwaremill . com)
 */
public class SysoutLog {

	public void info(String msg) {
			System.out.println("--- " + msg);
	}
}
