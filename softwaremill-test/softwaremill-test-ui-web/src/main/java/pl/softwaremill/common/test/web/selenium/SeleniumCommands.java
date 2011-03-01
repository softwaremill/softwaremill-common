package pl.softwaremill.common.test.web.selenium;

import org.testng.Assert;

import static pl.softwaremill.common.test.web.selenium.AbstractSeleniumTest.*;

/**
 * @author Pawel Wrzeszcz (pawel . wrzeszcz [at] gmail . com)
 * @author Jaroslaw Kijanowski (kijanowski [at] gmail . com)
 */
public class SeleniumCommands {

    private static final String WAIT_FOR_LOAD = "60000";
    private static final int TIME_OUT = 30;

    public static void waitForPageToLoad() {
        selenium.waitForPageToLoad(WAIT_FOR_LOAD);
    }

    public static void waitFor(final String xpath) throws Exception {
       waitFor(TIME_OUT, new Check() {
            @Override public boolean doCheck() { return selenium.isElementPresent(xpath); }
            @Override public String getErrorMessage() { return "Timed out on xpath: " + xpath; }
        });
    }

    public static void waitForElementPresent(String element) throws Exception {
        waitForElement(element, TIME_OUT, true);
    }

    public static void waitForElementNotPresent(String element) throws Exception {
        waitForElement(element, TIME_OUT, false);
    }

    public static void waitForElement(String element, boolean isPresent) throws Exception {
        waitForElement(element, TIME_OUT, isPresent);
    }

    public static void waitForElementPresent(String xpath, int timeout) throws Exception {
        waitForElement(xpath, timeout, true);
    }

    public static void waitForElement(final String element, int timeout, final boolean isPresent) throws Exception {
        waitFor(timeout, new Check() {
            @Override public boolean doCheck() { return selenium.isElementPresent(element) == isPresent; }
            @Override public String getErrorMessage() { return "Timed out on xpath: " + element; }
        });
    }


    public static void clickAndWait(String locator, String timeout) {
        selenium.click(locator);
        selenium.waitForPageToLoad(timeout);
    }

    public static void clickAndWait(String locator) {
        clickAndWait(locator, String.valueOf(WAIT_FOR_LOAD));
    }

    public static void waitForElementVisible(String locator) throws Exception {
        waitForElementVisible(locator, true);
    }

    public static void waitForElementVisible(final String locator, final boolean isVisible) throws Exception {
        waitFor(TIME_OUT, new Check() {
            @Override public boolean doCheck() { return selenium.isVisible(locator) == isVisible; }
            @Override public String getErrorMessage() { return "timeout"; }
        });
    }

    public static void waitForTextPresent(String text) throws Exception {
        waitForText(text, TIME_OUT, true);
    }

    public static void waitForTextPresent(String text, int timeout) throws Exception {
        waitForText(text, timeout, true);
    }

    public static void waitForTextNotPresent(String text) throws Exception {
        waitForText(text, TIME_OUT, false);
    }

    public static void waitForTextNotPresent(String text, int timeout) throws Exception {
        waitForText(text, timeout, false);
    }

    public static void waitForText(final String text, int timeout, final boolean isPresent) throws Exception {
        waitFor(timeout, new Check() {
            @Override public boolean doCheck() { return selenium.isTextPresent(text) == isPresent; }
            @Override public String getErrorMessage() { return "Timed out waiting for text: " + text; }
        });
    }

    public static void clickOncePresent(String xpath) throws Exception {
        clickOncePresent(xpath, TIME_OUT);
    }

    public static void clickOncePresent(String xpath, int timeout) throws Exception {
        waitForElementPresent(xpath, timeout);
        selenium.click(xpath);
    }

    public static void typeOncePresent(String xpath, String text) throws Exception {
        typeOncePresent(xpath, text, TIME_OUT);
    }

    public static void typeOncePresent(String xpath, String text, int timeout) throws Exception {
        waitForElementPresent(xpath, timeout);
        selenium.type(xpath, text);
    }

    public static void waitFor(int maxSeconds, Check check) throws Exception {
        for (int tries = 0; ; tries++) {
            if (tries >= maxSeconds * 10) Assert.fail(check.getErrorMessage());
            try {
                if (check.doCheck()) {
                    break;
                }
            }
            catch (Exception e) {
                // Empty
            }
            Thread.sleep(100);
        }
    }

    public static interface Check {
        boolean doCheck();
        String getErrorMessage();
    }
}
