package pl.softwaremill.common.test.web.jboss.server;

import pl.softwaremill.common.test.web.selenium.ServerProperties;

/**
 * @author Pawel Wrzeszcz (pawel [at] softwaremill . com)
 */
public class JBossASProvider {

	private final ServerProperties serverProperties;

	public JBossASProvider(ServerProperties serverProperties) {
		this.serverProperties = serverProperties;
	}

	public JBossAS createJBossASInstance() {
		int version = serverProperties.getAsVersion();
		switch (version) {
			case 5: return new JBossAS5or6(serverProperties);
			case 6: return new JBossAS5or6(serverProperties);
			case 7: return new JBossAS7(serverProperties);
			default: throw new IllegalArgumentException("Not supported JBossAS version: " + version);
		}
	}
}
