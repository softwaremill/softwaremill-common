package com.softwaremill.common.test.web.jboss.server;

import com.google.common.base.Charsets;
import com.softwaremill.common.test.util.MessageWaiter;
import com.softwaremill.common.test.web.jboss.SysoutLog;
import com.softwaremill.common.test.web.selenium.ServerProperties;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;

/**
 * @author Pawel Wrzeszcz (pawel [at] softwaremill . com)
 */
public abstract class AbstractJBossAS implements JBossAS {

	private final static SysoutLog log = new SysoutLog();

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
	abstract String startedLogMessage();

	protected Process startServer() throws Exception {
        String[] startCommand = startCommand();

		log.info("Starting JBoss server with command: " + Arrays.toString(startCommand));

        jbossProcess = Runtime.getRuntime().exec(startCommand);

		new MessageWaiter(jbossProcess).waitFor(startedLogMessage());

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
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(shutdownProcess.getOutputStream(), Charsets.UTF_8));
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
