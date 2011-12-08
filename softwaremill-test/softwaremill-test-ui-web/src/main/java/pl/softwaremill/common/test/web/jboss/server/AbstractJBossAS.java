package pl.softwaremill.common.test.web.jboss.server;

import pl.softwaremill.common.test.util.MessageWaiter;
import pl.softwaremill.common.test.web.jboss.SysoutLog;
import pl.softwaremill.common.test.web.selenium.ServerProperties;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Pawel Wrzeszcz (pawel [at] softwaremill . com)
 */
public abstract class AbstractJBossAS implements JBossAS {

	private final static SysoutLog log = new SysoutLog();

	private static final String STARTED_LOG_MESSAGE = "Started in";

	private Process jbossProcess;
	private boolean running;
	protected ServerProperties properties;


	public AbstractJBossAS(ServerProperties serverProperties) {
		this.properties = serverProperties;
		this.running = serverProperties.isRunning();
	}

	@Override
	public Process start() throws Exception {
		return (running) ? jbossProcess : startServer();
	}

	abstract String[] startCommand();
	abstract String[] shutdownCommand();


	protected Process startServer() throws Exception {
		log.info("Starting JBoss server");

		jbossProcess = Runtime.getRuntime().exec(startCommand());

		new MessageWaiter(jbossProcess).waitFor(STARTED_LOG_MESSAGE);

		log.info("JBoss started");

		return jbossProcess;
	}

	protected boolean winSystem() {
		String osName = System.getProperty("os.name");
		return osName != null && osName.contains("Windows");
	}

	@Override
	public void shutdown() throws IOException, InterruptedException {
		if (!running) {
			log.info("Stopping JBoss server");
			shutdownServer();
			log.info("JBoss Server stopped");
		}
	}

	protected void shutdownServer() throws IOException, InterruptedException {

		final Process shutdownProcess = Runtime.getRuntime().exec(shutdownCommand());

		if (winSystem()) {
			PrintWriter writer = new PrintWriter(shutdownProcess.getOutputStream());
			try {
				//On windows, user needs to press any key on the console
				//for the process to exit. Wait a bit for message "Press any key"
				new MessageWaiter(shutdownProcess).waitFor("Press any key to continue");
				writer.write("y");
				writer.flush();
			} finally {
				writer.close();
			}
		}
		shutdownProcess.waitFor();

        if (shutdownProcess.exitValue() != 0) {
            log.info("Failed to stop JBoss");
        } else {
            //Make sure jboss shuts down before leaving this method.
            jbossProcess.waitFor();
        }
	}
}
