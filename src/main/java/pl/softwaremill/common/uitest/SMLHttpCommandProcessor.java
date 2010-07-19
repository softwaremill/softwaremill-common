package pl.softwaremill.common.uitest;

import com.thoughtworks.selenium.HttpCommandProcessor;

/**
 * SML processor capable doing screenshots when something goes wrong
 *
 * User: szimano
 */
public class SMLHttpCommandProcessor extends HttpCommandProcessor{

    private Screenshotter screenshotter;

    public SMLHttpCommandProcessor(String serverHost, int serverPort, String browserStartCommand, String browserURL,
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
            // create screenshot
            screenshotter.doScreenshot();

            // and rethrow
            throw e;
        }
    }
}
