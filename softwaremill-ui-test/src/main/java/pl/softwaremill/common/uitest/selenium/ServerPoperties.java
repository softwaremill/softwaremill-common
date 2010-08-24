package pl.softwaremill.common.uitest.selenium;

/**
 *
 * @author maciek
 * @author Pawel Wrzeszcz
 */
public class ServerPoperties {

	private String serverHome;

	// Defaults
    private String configuration = "default";
    private int portset = 2;
    private boolean running = false;
	private int deploymentTimeoutMinutes = 5;

    /**
     * @param serverHome server home directory
     * @param configuration configuration name
     * @param portset port set
     * @param running Whether the server is running and should not be started/stopped
     */
    public ServerPoperties(String serverHome, String configuration, int portset, boolean running) {
        this.serverHome = serverHome;
        this.configuration = configuration;
        this.portset = portset;
        this.running = running;
    }

	public ServerPoperties(String serverHome) {
		this.serverHome = serverHome;
	}

    public String getServerHome() {
        return serverHome;
    }

    public String getConfiguration() {
        return configuration;
    }

    public int getPortset() {
        return portset;
    }

    public boolean isRunning() {
        return running;
    }

	public int getDeploymentTimeoutMinutes() {
		return deploymentTimeoutMinutes;
	}


	public ServerPoperties configuration(String configuration) {
		this.configuration = configuration;
		return this;
	}

	public ServerPoperties portset(int portset) {
		this.portset = portset;
		return this;
	}

	public ServerPoperties running(boolean running) {
		this.running = running;
		return this;
	}

	public ServerPoperties deploymentTimeoutMinutes(int deploymentTimeoutMinutes) {
		this.deploymentTimeoutMinutes = deploymentTimeoutMinutes;
		return this;
	}
}
