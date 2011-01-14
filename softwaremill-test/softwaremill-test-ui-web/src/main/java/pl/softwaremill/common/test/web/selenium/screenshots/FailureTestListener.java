package pl.softwaremill.common.test.web.selenium.screenshots;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import pl.softwaremill.common.test.web.selenium.AbstractSeleniumTest;

/**
 * User: szimano
 */
public class FailureTestListener extends TestListenerAdapter {

    private static final Logger log = LoggerFactory.getLogger(FailureTestListener.class);

    @Override
    public void onTestFailure(ITestResult tr) {
        // capture screenshot

        try {
            AbstractSeleniumTest.captureScreenshot();
        } catch (Exception e) {
            log.error("Couldn't capture screenshot", e);
        }
    }
}
