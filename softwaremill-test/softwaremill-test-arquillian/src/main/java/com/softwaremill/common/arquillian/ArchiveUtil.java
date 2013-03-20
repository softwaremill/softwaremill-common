package com.softwaremill.common.arquillian;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

/**
 * @author Pawel Wrzeszcz (pawel [at] softwaremill . com)
 */
public class ArchiveUtil {

	public static JavaArchive createArchive() {
		return ShrinkWrap.create(JavaArchive.class, "test.jar");
	}
}
