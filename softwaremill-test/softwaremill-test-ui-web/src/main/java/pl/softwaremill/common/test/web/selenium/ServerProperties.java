package pl.softwaremill.common.test.web.selenium;

/**
 *
 * @author maciek
 * @author Pawel Wrzeszcz
 * @author Jaroslaw Kijanowski
 */
public class ServerProperties {

	private String serverHome;

	// Defaults
    private String configuration = "default";
    private int portset = 2;
    private boolean running = false;
	private int deploymentTimeoutMinutes = 5;
    private String additionalSystemProperties = "";
    private int asVersion = 6;
    private boolean secured = false;
    private String username = "admin";
    private String password = "admin";

	public ServerProperties(String serverHome) {
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

    public String getAdditionalSystemProperties() {
        return additionalSystemProperties;
    }

    public ServerProperties configuration(String configuration) {
		this.configuration = configuration;
		return this;
	}

	public ServerProperties portset(int portset) {
		this.portset = portset;
		return this;
	}

	public ServerProperties running(boolean running) {
		this.running = running;
		return this;
	}

	public ServerProperties deploymentTimeoutMinutes(int deploymentTimeoutMinutes) {
		this.deploymentTimeoutMinutes = deploymentTimeoutMinutes;
		return this;
	}

    public ServerProperties additionalSystemProperties(String additionalSystemProperties) {
        this.additionalSystemProperties = additionalSystemProperties;
        return this;
    }

    public int getAsVersion() {
        return asVersion;
    }

    public ServerProperties setAsVersion(int asVersion) {
        this.asVersion = asVersion;
        return this;
    }

    public boolean isSecured() {
        return secured;
    }

    public ServerProperties setSecured(boolean secured) {
        this.secured = secured;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public ServerProperties setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public ServerProperties setPassword(String password) {
        this.password = password;
        return this;
    }
}
