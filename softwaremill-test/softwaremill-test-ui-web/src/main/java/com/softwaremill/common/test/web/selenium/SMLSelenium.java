package com.softwaremill.common.test.web.selenium;

import com.thoughtworks.selenium.DefaultSelenium;
import com.softwaremill.common.test.web.selenium.screenshots.ScreenshotHttpCommandProcessor;
import com.softwaremill.common.test.web.selenium.screenshots.Screenshotter;

/**
 * Selenium capable of doing screenshots
 *
 * User: szimano
 */
public class SMLSelenium extends DefaultSelenium {

    public SMLSelenium(String serverHost, int serverPort, String browserStartCommand, String browserURL,
                       Screenshotter screenshotter) {
        super(serverHost, serverPort, browserStartCommand, browserURL);

        // use our command processor
        this.commandProcessor = new ScreenshotHttpCommandProcessor(serverHost, serverPort, browserStartCommand, browserURL,
                screenshotter);
    }

	public void open(String url) {
     	commandProcessor.doCommand("open", new String[] {url,"true"});
    }
}
