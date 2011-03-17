package pl.softwaremill.common.test.web.selenium.screenshots;

import com.thoughtworks.selenium.HttpCommandProcessor;

/**
 * SML processor capable doing screenshots when something goes wrong
 *
 * User: szimano
 */
public class ScreenshotHttpCommandProcessor extends HttpCommandProcessor {

    private boolean capturingScreenshot = false;

    private Screenshotter screenshotter;

    public ScreenshotHttpCommandProcessor(String serverHost, int serverPort, String browserStartCommand, String browserURL,
                                   Screenshotter screenshotter) {
        super(serverHost, serverPort, browserStartCommand, browserURL);
        this.screenshotter = screenshotter;
    }

    @Override
    public String doCommand(String commandName, String[] args) {
        try {
            return super.doCommand(commandName, args);
        }
        catch (RuntimeException e) {
            if (!capturingScreenshot) {
                //Prevent infinite loop, when
                // screenshotter.doScreenshot() throws exception
                capturingScreenshot = true;
                // create screenshot
                screenshotter.doScreenshot();
            }
            capturingScreenshot = false;

            // and rethrow
            throw e;
        }
    }
}
