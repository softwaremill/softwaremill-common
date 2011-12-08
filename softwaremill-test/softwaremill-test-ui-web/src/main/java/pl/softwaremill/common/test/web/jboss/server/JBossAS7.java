package pl.softwaremill.common.test.web.jboss.server;

import pl.softwaremill.common.test.web.selenium.ServerProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Pawel Wrzeszcz (pawel [at] softwaremill . com)
 */
public class JBossAS7 extends AbstractJBossAS {

	private static final String STARTED_LOG_MESSAGE = "started in";

	public JBossAS7(ServerProperties serverProperties) {
		super(serverProperties);
		verifyDefaultPortset();
	}

	private void verifyDefaultPortset() {
		if (properties.getPortset() != 0) {
			throw new IllegalArgumentException("Only support for default (0) portset is implemented for JBoss AS 7");
		}
	}

	@Override
	protected String[] startCommand() {
		return new String[]{
				properties.getServerHome() + createRunScript(),
				getConfiguration(),
				properties.getAdditionalSystemProperties()};
	}

	private String getConfiguration() {
		return properties.getConfiguration().equals("default") ? "" : "-c " + properties.getConfiguration();
	}

	private String createRunScript() {
		return (winSystem()) ? "/bin/standalone.bat" : "/bin/standalone.sh";
	}

	protected String[] shutdownCommand() {

        List<String> paramList = new ArrayList<String>();

        paramList.add(properties.getServerHome() + shutdownScript() + " --connect command=:shutdown");

        if(properties.isSecured()){
            paramList.add("--user=" + properties.getUsername());
            paramList.add("--password=" + properties.getPassword());
        }
		return paramList.toArray(new String[0]);
	}

	private String shutdownScript() {
		return winSystem() ? "/bin/jboss-admin.bin" : "/bin/jboss-admin.sh";
	}

	@Override
	public String getDeployDir() {
		return properties.getServerHome() + "/standalone/deployments/";
	}

	public String getServerLogPath() {
		return properties.getServerHome() + "/standalone/log/server.log";
	}

	@Override
	String startedLogMessage() {
		return STARTED_LOG_MESSAGE;
	}
}
