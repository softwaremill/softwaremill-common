package pl.softwaremill.common.test.web.email;

import com.dumbster.smtp.SimpleSmtpServer;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import pl.softwaremill.common.test.util.SimpleSmtpServerStarter;


/**
 *  Starts and stops mock email server before/after test suite.
 *  The 'inbox' is common for all tests.
 *
 *  Method to return smtp port needs to be implemented
 *
 * @author maciek
 */
public abstract class AbstractEmailServerRunner {

    protected static SimpleSmtpServer emailServer;

	@BeforeSuite(alwaysRun = true)
	public void startEmailServer() {
        int port = getSmtpServerPort();
		System.out.println("--- Starting email server on port " + port);
		emailServer = SimpleSmtpServerStarter.start(port);
	}

	@AfterSuite(alwaysRun = true)
	public void stopEmailServer() {
		System.out.println("--- Stopping email server");
		emailServer.stop();
	}

    /**
     * @return smtp port used by tested app
     */
    protected abstract int getSmtpServerPort();
}
