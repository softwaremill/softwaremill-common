package pl.softwaremill.common.test.web.selenium;

import org.testng.Assert;

import static pl.softwaremill.common.test.web.selenium.AbstractSeleniumTest.fail;
import static pl.softwaremill.common.test.web.selenium.AbstractSeleniumTest.selenium;

/**
 * @author Pawel Wrzeszcz (pawel . wrzeszcz [at] gmail . com)
 */
public class SeleniumCommands {

	public static void waitForPageToLoad() {
		selenium.waitForPageToLoad("60000");
	}

	public static void waitFor(String xpath) throws Exception {
		for (int second = 0; ; second++) {
			if (second >= 30) {
				fail("timeout on xpath: " + xpath);
			}
			if (selenium.isElementPresent(xpath)) {
				break;
			}
			Thread.sleep(1000);
		}
	}

	public static void waitForElementPresent(String element) throws Exception {
		waitForElement(element, true);
	}

	public static void waitForElementNotPresent(String element) throws Exception {
		waitForElement(element, false);
	}

	public static void waitForElement(String element, boolean isPresent) throws Exception {
		for (int second = 0; ; second++) {
			if (second >= 30) Assert.fail("timeout");
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

    public static void waitForElementVisible(String locator) throws Exception {
        waitForElementVisible(locator, true);
    }

    public static void waitForElementVisible(String locator, boolean isVisible) throws Exception {
		for (int second = 0; ; second++) {
			if (second >= 30) Assert.fail("timeout");
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
}
