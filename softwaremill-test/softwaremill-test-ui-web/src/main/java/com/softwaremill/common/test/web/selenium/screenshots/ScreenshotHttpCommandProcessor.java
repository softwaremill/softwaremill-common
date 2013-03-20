package com.softwaremill.common.test.web.selenium.screenshots;

import com.thoughtworks.selenium.CommandProcessor;
import com.thoughtworks.selenium.HttpCommandProcessor;

/**
 * SML processor capable doing screenshots when something goes wrong
 *
 * User: szimano
 */
public class ScreenshotHttpCommandProcessor implements CommandProcessor {

    private boolean capturingScreenshot = false;

    private Screenshotter screenshotter;
    private final CommandProcessor delegate;

    public ScreenshotHttpCommandProcessor(CommandProcessor delegate, Screenshotter screenshotter) {
        this.delegate = delegate;
        ScreenshotHttpCommandProcessor.this.screenshotter = screenshotter;
    }

    public ScreenshotHttpCommandProcessor(String serverHost, int serverPort, String browserStartCommand,
                                          String browserURL, Screenshotter screenshotter) {
        this(new HttpCommandProcessor(serverHost, serverPort, browserStartCommand, browserURL), screenshotter);
    }

    @Override
    public String doCommand(String commandName, String[] args) {
        try {
            String result = delegate.doCommand(commandName, args);
            capturingScreenshot = false;

            return result;
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

    public String getRemoteControlServerLocation() {
        return delegate.getRemoteControlServerLocation();
    }

    public void setExtensionJs(String extensionJs) {
        delegate.setExtensionJs(extensionJs);
    }

    public void start() {
        delegate.start();
    }

    public void start(String optionsString) {
        delegate.start(optionsString);
    }

    public void start(Object optionsObject) {
        delegate.start(optionsObject);
    }

    public void stop() {
        delegate.stop();
    }

    public String getString(String commandName, String[] args) {
        return delegate.getString(commandName, args);
    }

    public String[] getStringArray(String commandName, String[] args) {
        return delegate.getStringArray(commandName, args);
    }

    public Number getNumber(String commandName, String[] args) {
        return delegate.getNumber(commandName, args);
    }

    public Number[] getNumberArray(String commandName, String[] args) {
        return delegate.getNumberArray(commandName, args);
    }

    public boolean getBoolean(String commandName, String[] args) {
        return delegate.getBoolean(commandName, args);
    }

    public boolean[] getBooleanArray(String commandName, String[] args) {
        return delegate.getBooleanArray(commandName, args);
    }

}
