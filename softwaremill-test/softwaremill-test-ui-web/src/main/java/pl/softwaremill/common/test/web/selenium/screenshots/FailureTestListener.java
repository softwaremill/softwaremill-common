package pl.softwaremill.common.test.web.selenium.screenshots;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import pl.softwaremill.common.test.web.selenium.AbstractSeleniumTest;

import java.util.HashSet;
import java.util.Set;

/**
 * User: szimano
 */
public class FailureTestListener extends TestListenerAdapter {

    private static final Logger log = LoggerFactory.getLogger(FailureTestListener.class);

    public static final Set<ITestResult> failedResults = new HashSet<ITestResult>();

    @Override
    public void onTestFailure(ITestResult tr) {
        // capture screenshot

        try {
            // only perform screenshot if this result wasn't handled yet (to avoid duplicates)
            if (failedResults.add(tr)) {
                AbstractSeleniumTest.captureScreenshot(tr.getName());
            }
        } catch (Exception e) {
            log.error("Couldn't capture screenshot", e);
        }
    }
}
