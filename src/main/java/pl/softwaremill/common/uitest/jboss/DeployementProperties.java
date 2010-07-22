package pl.softwaremill.common.uitest.jboss;

/**
 *
 * @author maciek
 */
public class DeployementProperties {

    private String appVersion;
    
    private String earPath;
    
    private String rorPath;

    /**
     * @param appVersion
     * @param earPath
     * @param rorPath
     */
    public DeployementProperties(String appVersion, String earPath, String rorPath) {
        this.appVersion = appVersion;
        this.earPath = earPath;
        this.rorPath = rorPath;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public String getEarPath() {
        return earPath;
    }

    public String getRorPath() {
        return rorPath;
    }
    
    
    
}
