package pl.softwaremill.common.util;

/**
 * Constants to avoid code comments like
 * <code>private static long TIMEOUT = 10000l; // 10s</code>
 *
 * @author Pawel Wrzeszcz (pawel [at] softwaremill . com)
 */
public class SecsToMills {

	public static long ONE_SECOND = 1000l;
	public static long TWO_SECONDS = 2 * ONE_SECOND;
	public static long TEN_SECONDS = 10 * ONE_SECOND;

	public static long ONE_MINUTE = 60 * ONE_SECOND;
	public static long TWO_MINUTES = 2 * ONE_MINUTE;
}
