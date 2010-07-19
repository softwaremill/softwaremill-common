package pl.softwaremill.common.uitest;

import com.thoughtworks.selenium.DefaultSelenium;

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
        this.commandProcessor = new SMLHttpCommandProcessor(serverHost, serverPort, browserStartCommand, browserURL,
                screenshotter);
    }
}
