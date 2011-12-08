package pl.softwaremill.common.test.web.jboss.server;

import pl.softwaremill.common.test.util.MessageWaiter;
import pl.softwaremill.common.test.web.jboss.SysoutLog;
import pl.softwaremill.common.test.web.selenium.ServerProperties;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Pawel Wrzeszcz (pawel [at] softwaremill . com)
 */
public class JBossASv56 implements JBossAS {

	private final static SysoutLog log = new SysoutLog();

	private static final String STARTED_LOG_MESSAGE = "Started in";

	private Process jbossProcess;
	private boolean running;
	private ServerProperties properties;


	public JBossASv56(ServerProperties serverProperties) {
		this.properties = serverProperties;
		this.running = serverProperties.isRunning();
	}

	@Override
	public Process start() throws Exception {
		return (running) ? jbossProcess : startServer();
	}

	protected Process startServer() throws Exception {
		log.info("Starting JBoss server");

		jbossProcess = Runtime.getRuntime().exec(new String[]{
				properties.getServerHome() + createRunScript(), "-c",
				properties.getConfiguration(),
				createPortSetCommand(),
				properties.getAdditionalSystemProperties()});

		new MessageWaiter(jbossProcess).waitFor(STARTED_LOG_MESSAGE);

		log.info("JBoss started");

		return jbossProcess;
	}

	private String createPortSetCommand() {
		if (properties.getPortset() <= 0) {
			return "";
		}

		return "-Djboss.service.binding.set=ports-0" + properties.getPortset();
	}

	private String createRunScript() {
		if (winSystem()) {
			return "/bin/run.bat";
		}
		return "/bin/run.sh";
	}

	private boolean winSystem() {
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

        List<String> paramList = new ArrayList<String>();

        // bin/shutdown.{sh, bat}
        paramList.add(properties.getServerHome() + createShutdownScript());

        if(properties.getAsVersion() == 5){
            // JBoss5
            paramList.add("--server=localhost:1" + properties.getPortset() + "99");
        } else {
            // JBoss6 and above
            paramList.add("--host=localhost");
            paramList.add("--port=1" + properties.getPortset() + "90");
        }

        // shutdown
        paramList.add("-S");

        if(properties.isSecured()){
            paramList.add("-u " + properties.getUsername());
            paramList.add("-p " + properties.getPassword());
        }

		final Process shutdownProcess = Runtime.getRuntime().exec(paramList.toArray(new String[0]));

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

	private String createShutdownScript() {
		if (winSystem()) {
			return "/bin/shutdown.bat";
		}
		return "/bin/shutdown.sh";
	}

	@Override
	public String getDeployDir() {
		return properties.getServerHome() + "/server/" + properties.getConfiguration() + "/deploy/";
	}

	public String getServerLogPath() {
		return properties.getServerHome() + "/server/" + properties.getConfiguration() + "/log/server.log";
	}
}
