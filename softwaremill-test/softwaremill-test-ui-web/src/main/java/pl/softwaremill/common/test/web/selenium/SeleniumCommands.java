package pl.softwaremill.common.test.web.selenium;

import org.testng.Assert;

import static pl.softwaremill.common.test.web.selenium.AbstractSeleniumTest.fail;
import static pl.softwaremill.common.test.web.selenium.AbstractSeleniumTest.selenium;

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

    public static void waitFor(String xpath) throws Exception {
        for (int second = 0; ; second++) {
            if (second >= TIME_OUT) {
                Assert.fail("Timed out on xpath: " + xpath);
            }
            if (selenium.isElementPresent(xpath)) {
                break;
            }
            Thread.sleep(1000);
        }
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

    public static void waitForElement(String element, int timeout, boolean isPresent) throws Exception {
        for (int second = 0; ; second++) {
            if (second >= timeout) Assert.fail("Timed out on xpath: " + element);
            try {
                if (selenium.isElementPresent(element) == isPresent) {
                    break;
                }
            }
            catch (Exception e) {
                // Empty
            }
            Thread.sleep(1000);
        }
    }


    public static void clickAndWait(String locator, String timeout) {
        selenium.click(locator);
        selenium.waitForPageToLoad(timeout);
    }

    public static void clickAndWait(String locator) {
        clickAndWait(locator, String.valueOf(TIME_OUT));
    }

    public static void waitForElementVisible(String locator) throws Exception {
        waitForElementVisible(locator, true);
    }

    public static void waitForElementVisible(String locator, boolean isVisible) throws Exception {
        for (int second = 0; ; second++) {
            if (second >= TIME_OUT) Assert.fail("timeout");
            try {
                if (selenium.isVisible(locator) == isVisible) {
                    break;
                }
            }
            catch (Exception e) {
                // Empty
            }
            Thread.sleep(1000);
        }
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

    public static void waitForText(String text, int timeout, boolean isPresent) throws Exception {
        for (int second = 0; ; second++) {
            if (second >= timeout) Assert.fail("Timed out waiting for text: " + text);
            try {
                if (selenium.isTextPresent(text) == isPresent) {
                    break;
                }
            }
            catch (Exception e) {
                // Empty
            }
            Thread.sleep(1000);
        }
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
}
