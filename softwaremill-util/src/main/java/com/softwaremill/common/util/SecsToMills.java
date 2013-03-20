package com.softwaremill.common.util;

/**
 * Constants to avoid code comments like
 * <code>private static long TIMEOUT = 10000l; // 10s</code>
 *
 * @author Pawel Wrzeszcz (pawel [at] softwaremill . com)
 */
public class SecsToMills {

	public static final long ONE_SECOND = 1000L;
	public static final long TWO_SECONDS = 2 * ONE_SECOND;
	public static final long TEN_SECONDS = 10 * ONE_SECOND;

	public static final long ONE_MINUTE = 60 * ONE_SECOND;
	public static final long TWO_MINUTES = 2 * ONE_MINUTE;
}
