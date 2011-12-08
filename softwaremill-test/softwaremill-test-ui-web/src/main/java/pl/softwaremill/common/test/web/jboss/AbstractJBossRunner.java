package pl.softwaremill.common.test.web.jboss;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import pl.softwaremill.common.test.util.MessageWaiter;
import pl.softwaremill.common.test.web.jboss.server.JBossAS;
import pl.softwaremill.common.test.web.jboss.server.JBossASProvider;
import pl.softwaremill.common.test.web.selenium.ServerProperties;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.testng.Assert.fail;

/**
 * @author maciek
 * @author Pawel Wrzeszcz
 * @author Jaroslaw Kijanowski
 * @author Pawel Stawicki
 * @author Marcin Jekot
 */
public abstract class AbstractJBossRunner {

	private JBossAS jboss;

	protected final static SysoutLog log = new SysoutLog();

	private boolean deploymentComplete = false;

	private static final int MILLISECONDS_IN_MINUTE = 60 * 1000;

	protected abstract ServerProperties getServerProperties();

	protected abstract Deployment[] getDeployments();

	private Process tailProcess;

    private boolean timedout;

	@BeforeSuite
	public void start() throws Exception {
		scheduleTimeout();
		undeploy(); // Clean old deployments

		jboss = new JBossASProvider(getServerProperties()).createJBossASInstance();
		tailProcess = jboss.start();

		deploy();
	}

	@AfterSuite(alwaysRun = true)
	public void shutdown() throws Exception {
		undeploy();
		jboss.shutdown();
		publishLog();
	}

	private Process getTailProcess() throws IOException {
		if (tailProcess == null) {
			// this happens when jboss was already started
			tailProcess = Runtime.getRuntime().exec(
					new String[]{"tail", "-f", jboss.getServerLogPath()});
		}

		return tailProcess;
	}

	private void deploy() throws Exception {
		for (Deployment deployment : getDeployments()) {
			deployment.deploy(jboss.getDeployDir());
			if (deployment.getWaitForMessage() != null) {
                waitForMessageOrTimeout(deployment);
                if (timedout) {
                   fail("Server startup/deploy timeout exceeded.");
                }
			} else {
				Thread.sleep(deployment.getWaitMillis());
			}
		}
		deploymentComplete = true;

		// close the tail process so it doesn't overload
		tailProcess.getInputStream().close();
	}

    private void waitForMessageOrTimeout(final Deployment deployment) throws Exception {

        final CountDownLatch latch = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    new MessageWaiter(getTailProcess()).waitFor(deployment.getWaitForMessage());
                    latch.countDown();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }).start();

        while (!timedout && !latch.await(50, MILLISECONDS)) { }

    }

	private void undeploy() throws Exception {
		for (Deployment deployment : getDeployments()) {
			deployment.undeploy(jboss.getDeployDir());
		}
	}

	private void scheduleTimeout() {
		new Thread(
				new Runnable() {

					@Override
					public void run() {
						try {
							Thread.sleep(((long) getServerProperties().getDeploymentTimeoutMinutes()) * MILLISECONDS_IN_MINUTE);
							if (!deploymentComplete) {
                                timedout = true;
                                log.info("Server startup/deploy timeout exceeded.");
							}
						} catch (InterruptedException e) {
							// do nothing
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					}
				}
		).start();
	}

	protected void publishLog() throws IOException {
		File tmpLogFile = File.createTempFile("jboss_log", ".txt");
		File logFile = new File(jboss.getServerLogPath());
		FileUtils.copyFile(logFile, tmpLogFile);

		System.out.println("##teamcity[publishArtifacts '" + tmpLogFile.getAbsolutePath() + "']");

		deleteLog();
	}

	private void deleteLog() {
		boolean deleted = new File(jboss.getServerLogPath()).delete();
		if (!deleted) {
			log.info("Unable to delete JBoss log");
		}
	}
}
