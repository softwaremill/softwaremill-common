package pl.softwaremill.common.test.web.jboss.server;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pl.softwaremill.common.test.util.Execution;
import pl.softwaremill.common.test.web.selenium.ServerProperties;

import java.util.Arrays;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Pawel Wrzeszcz (pawel [at] softwaremill . com)
 */
public class JBossASTest {

	private static final String SERVER_HOME = "/Users/john01/projectZ/torquebox/jboss";

	@Test(dataProvider = "getServerProperties")
	public void shouldStartJBossWithProperCommand(ServerProperties properties, String expectedCommand) throws Exception {
	    // Given
		AbstractJBossAS jboss = (AbstractJBossAS) new JBossASProvider(properties).createJBossASInstance();

		// When
		String[] command = jboss.startCommand();

	    // Then
		assertThat(normalize(command)).isEqualTo(expectedCommand);
	}

	@DataProvider
	public Object[][] getServerProperties() {
	    return new Object[][] {
	        {
				new ServerProperties(SERVER_HOME).asVersion(5),
				SERVER_HOME + "/bin/run.sh -c default -Djboss.service.binding.set=ports-02"
			},
			{
				new ServerProperties(SERVER_HOME).asVersion(5).secured(true).username("scott").password("tiger").configuration("myconf").portset(0),
				SERVER_HOME + "/bin/run.sh -c myconf"
			},
			{
				new ServerProperties(SERVER_HOME).asVersion(6).additionalSystemProperties("-Dmyprop=val"),
				SERVER_HOME + "/bin/run.sh -c default -Djboss.service.binding.set=ports-02 -Dmyprop=val"
			},
			{
				new ServerProperties(SERVER_HOME).asVersion(7).portset(0),
				SERVER_HOME + "/bin/standalone.sh"
			},
			{
				new ServerProperties(SERVER_HOME).asVersion(7).configuration("mystandalone.xml").portset(0),
				SERVER_HOME + "/bin/standalone.sh -c mystandalone.xml"
			},
	    };
	}

	@Test(dataProvider = "getServerPropertiesShutdown")
	public void shouldShutdownJBossWithProperCommnd(ServerProperties properties, String expectedCommand) throws Exception {
	    // Given
	     AbstractJBossAS jboss = (AbstractJBossAS) new JBossASProvider(properties).createJBossASInstance();

		// When
		String[] command = jboss.shutdownCommand();

	    // Then
		assertThat(normalize(command)).isEqualTo(expectedCommand);
	}

	@DataProvider
	public Object[][] getServerPropertiesShutdown() {
	    return new Object[][] {
	        {
				new ServerProperties(SERVER_HOME).asVersion(5),
				SERVER_HOME + "/bin/shutdown.sh --server=localhost:1299 -S"
			},
			{
				new ServerProperties(SERVER_HOME).asVersion(5).secured(true).username("scott").password("tiger").configuration("myconf").portset(0),
				SERVER_HOME + "/bin/shutdown.sh --server=localhost:1099 -S -u scott -p tiger"
			},
			{
				new ServerProperties(SERVER_HOME).asVersion(6).additionalSystemProperties("-Dmyprop=val"),
				SERVER_HOME + "/bin/shutdown.sh --host=localhost --port=1290 -S"
			},
			{
				new ServerProperties(SERVER_HOME).asVersion(7).portset(0),
				SERVER_HOME + "/bin/jboss-admin.sh --connect command=:shutdown"
			},
			{
				new ServerProperties(SERVER_HOME).asVersion(7).portset(0).secured(true).username("scott").password("tiger"),
				SERVER_HOME + "/bin/jboss-admin.sh --connect command=:shutdown --user=scott --password=tiger"
			},
	    };
	}

	@Test
	public void shouldJBossAS7OnlySupportDefaultPortset() throws Exception {
	      // Given
		final ServerProperties properties = new ServerProperties(SERVER_HOME).asVersion(7).configuration("mystandalone.xml").portset(1);

		// When
		Execution execution = new Execution() {

			@Override
			protected void execute() throws Exception {
				new JBossASProvider(properties).createJBossASInstance();
			}
		};

	    // Then
		//noinspection ThrowableResultOfMethodCallIgnored
		assertThat(execution.getException()).isNotNull().isInstanceOf(IllegalArgumentException.class);
	}

	private static String normalize(String[] command) {
		return Arrays.toString(command).replaceAll("[,\\[\\]]","").trim();
	}
}
