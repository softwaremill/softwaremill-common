package pl.softwaremill.common.util;

import java.util.Arrays;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class ClassUtil {
	/**
	 * @param names Class names to check.
	 * @return Name of the class that is present in the classpath, one of {@code names}.
	 * @throws RuntimeException If none of the given classes is found.
	 */
	public static String findClassPresentInClasspath(String... names) {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		for (String name : names) {
			try {
				cl.loadClass(name);
				return name;
			} catch (ClassNotFoundException ignore) {
				// Empty
			}
		}

		throw new RuntimeException("None of the given classes where found on the classpath: " + Arrays.toString(names));
	}

	public static <T> T newInstance(String className, Class<T> expectedType) {
		try {
			return expectedType.cast(Thread.currentThread().getContextClassLoader().loadClass(className).newInstance());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
