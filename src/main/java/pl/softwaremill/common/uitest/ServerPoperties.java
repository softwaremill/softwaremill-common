package pl.softwaremill.common.uitest;

/**
 *
 * @author maciek
 */
public class ServerPoperties {

    private String serverHome;
    
    private String configuration;
    
    private int port;
    
    private boolean running;

    /**
     * @param serverHome
     * @param configuration
     * @param port
     * @param running Whether the server is running and should not be started/stopped
     */
    public ServerPoperties(String serverHome, String configuration, int port, boolean running) {
        this.serverHome = serverHome;
        this.configuration = configuration;
        this.port = port;
        this.running = running;
    }

    public String getServerHome() {
        return serverHome;
    }

    public String getConfiguration() {
        return configuration;
    }

    public int getPort() {
        return port;
    }

    public boolean isRunning() {
        return running;
    }

    
}
