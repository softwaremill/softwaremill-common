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
        super.onTestFailure(tr);
        // capture screenshot
        try {
            if (shouldMakeScreenShot(tr)) {
                AbstractSeleniumTest.captureScreenshot(tr.getName());
            }
        } catch (Exception e) {
            log.error("Couldn't capture screenshot", e);
        }
    }

    /**
     * make screenshot only for Selenium test and if the result wasn't handled yet (to avoid duplicates)
     */
    private boolean shouldMakeScreenShot(ITestResult tr) {
        return AbstractSeleniumTest.class.isAssignableFrom(tr.getTestClass().getRealClass()) && failedResults.add(tr);
    }

}
