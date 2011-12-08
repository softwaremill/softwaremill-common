package pl.softwaremill.common.test.web.jboss.server;

import java.io.IOException;

/**
 * @author Pawel Wrzeszcz (pawel [at] softwaremill . com)
 */
public interface JBossAS {

	Process start() throws Exception;

	void shutdown() throws IOException, InterruptedException;

	String getDeployDir();

	String getServerLogPath();
}
