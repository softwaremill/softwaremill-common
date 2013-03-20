package com.softwaremill.common.test.web.selenium.screenshots;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import com.softwaremill.common.test.web.selenium.AbstractSeleniumTest;

/**
 * It isn't a real test class, it's used in {@link FailureTestListenerTest}
 */
public class AnSeleniumTest extends AbstractSeleniumTest {

    @BeforeSuite
    @Override
    public void setupSelenium() throws Exception {
    }

    @AfterSuite
    @Override
    public void stopSelenium() throws Exception {
    }

    @BeforeTest
    @Override
    public void setupBrowser() throws Exception {
    }

    @AfterTest
    @Override
    public void stopBrowser() throws Exception {
    }

}
