package com.softwaremill.common.test.web.jboss.server;

import com.softwaremill.common.test.web.selenium.ServerProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Pawel Wrzeszcz (pawel [at] softwaremill . com)
 */
public class JBossAS5or6 extends AbstractJBossAS {

	private static final String STARTED_LOG_MESSAGE = "Started in";

	public JBossAS5or6(ServerProperties serverProperties) {
		super(serverProperties);
	}

	@Override
	protected String[] startCommand() {
		return new String[]{
				properties.getServerHome() + createRunScript(), "-c",
				properties.getConfiguration(),
				createPortSetCommand(),
				properties.getAdditionalSystemProperties()};
	}

	private String createPortSetCommand() {
		if (properties.getPortset() <= 0) {
			return "";
		}

		return "-Djboss.service.binding.set=ports-0" + properties.getPortset();
	}

	private String createRunScript() {
		return (winSystem()) ? "/bin/run.bat" : "/bin/run.sh";
	}

	protected String[] shutdownCommand() {

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
		return paramList.toArray(new String[0]);
	}

	@Override
	String startedLogMessage() {
		return STARTED_LOG_MESSAGE;
	}

	private String createShutdownScript() {
		return winSystem() ? "/bin/shutdown.bat" : "/bin/shutdown.sh";
	}

	@Override
	public String getDeployDir() {
		return properties.getServerHome() + "/server/" + properties.getConfiguration() + "/deploy/";
	}

	public String getServerLogPath() {
		return properties.getServerHome() + "/server/" + properties.getConfiguration() + "/log/server.log";
	}
}
