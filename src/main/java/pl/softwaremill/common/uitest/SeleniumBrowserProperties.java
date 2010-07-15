package pl.softwaremill.common.uitest;

/**
 * Properties for selenium browser
 * 
 * @author maciek
 */
public class SeleniumBrowserProperties {

    private String browserCommand;
    
    private String browserURL;
    
    private String browserPort;

    /**
     * @param browserCommand For example "*firefox"
     * @param browserURL
     * @param browserPort
     */
    public SeleniumBrowserProperties(String browserCommand, String browserURL, String browserPort) {
        this.browserCommand = browserCommand;
        this.browserURL = browserURL;
        this.browserPort = browserPort;
    }

    public String getBrowserCommand() {
        return browserCommand;
    }

    public void setBrowserCommand(String browserCommand) {
        this.browserCommand = browserCommand;
    }

    public String getBrowserURL() {
        return browserURL;
    }

    public void setBrowserURL(String browserURL) {
        this.browserURL = browserURL;
    }

    public String getBrowserPort() {
        return browserPort;
    }

    public void setBrowserPort(String browserPort) {
        this.browserPort = browserPort;
    }
}
