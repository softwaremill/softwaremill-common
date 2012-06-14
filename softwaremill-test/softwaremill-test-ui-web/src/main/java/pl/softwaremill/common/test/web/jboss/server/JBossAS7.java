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
    }

    @Override
    protected String[] startCommand() {
        return new String[]{
                properties.getServerHome() + createRunScript(),
                getConfiguration(),
                createPortOffsetCommand(),
                properties.getAdditionalSystemProperties()};
    }

    private String createPortOffsetCommand() {
        if (properties.getPortset() <= 0) {
            return "";
        }
        int offset = properties.getPortset() * 100;
        return "-Djboss.socket.binding.port-offset=" + offset;
    }

    private String getConfiguration() {
        return properties.getConfiguration().equals("default") ? "" : "-c " + properties.getConfiguration();
    }

    private String createRunScript() {
        return (winSystem()) ? "/bin/standalone.bat" : "/bin/standalone.sh";
    }

    protected String[] shutdownCommand() {

        List<String> paramList = new ArrayList<String>();

        paramList.add(properties.getServerHome() + shutdownScript());
        paramList.add("--connect");
        paramList.add("command=:shutdown");

        if (properties.isSecured()) {
            paramList.add("--user=" + properties.getUsername());
            paramList.add("--password=" + properties.getPassword());
        }
        if (properties.getPortset() > 0) {
            int controllerPort = 9999 + (properties.getPortset() * 100);
            paramList.add("--controller=localhost:" + controllerPort);
        }
        return paramList.toArray(new String[0]);
    }

    private String shutdownScript() {
        return winSystem() ? "/bin/jboss-cli.bin" : "/bin/jboss-cli.sh";
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
