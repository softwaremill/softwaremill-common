package pl.softwaremill.common.uitest.selenium;

/**
 *
 * @author maciek
 */
public class ServerPoperties {

    private String serverHome;
    
    private String configuration;
    
    private int portset;
    
    private boolean running;

    /**
     * @param serverHome
     * @param configuration
     * @param portset port set
     * @param running Whether the server is running and should not be started/stopped
     */
    public ServerPoperties(String serverHome, String configuration, int portset, boolean running) {
        this.serverHome = serverHome;
        this.configuration = configuration;
        this.portset = portset;
        this.running = running;
    }

    public String getServerHome() {
        return serverHome;
    }

    public String getConfiguration() {
        return configuration;
    }

    public int getPortset() {
        return portset;
    }

    public boolean isRunning() {
        return running;
    }

    
}
