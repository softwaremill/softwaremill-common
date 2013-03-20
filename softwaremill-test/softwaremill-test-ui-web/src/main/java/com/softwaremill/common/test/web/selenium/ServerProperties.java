package com.softwaremill.common.test.web.selenium;

import com.google.common.base.Function;
import com.google.common.base.Joiner;

import javax.annotation.Nullable;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Collections2.transform;

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

    public ServerProperties additionalSystemPropertiesFrom(Map<String, String> propertyMap) {
        this.additionalSystemProperties = Joiner.on(" ").join(transform(checkNotNull(propertyMap).entrySet(), new Function<Map.Entry<String, String>, String>() {
            @Override
            public String apply(@Nullable Map.Entry<String, String> input) {
                return "-D" + input.getKey() + "=" + input.getValue();
            }
        }));
        return this;
    }

    public int getAsVersion() {
        return asVersion;
    }

    public ServerProperties asVersion(int asVersion) {
        this.asVersion = asVersion;
        return this;
    }

    public boolean isSecured() {
        return secured;
    }

    public ServerProperties secured(boolean secured) {
        this.secured = secured;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public ServerProperties username(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public ServerProperties password(String password) {
        this.password = password;
        return this;
    }
}
