package pl.softwaremill.common.conf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JBoss 7 configuration provider
 */
public class JBoss7DeployPropertiesProvider extends FolderPropertiesProvider {

    private static final Logger log = LoggerFactory.getLogger(JBoss7DeployPropertiesProvider.class);
    public static final String JBOSS_CONFIG_DIR_PROPERTY = "jboss.server.config.dir";

    @Override
    public String lookupConfigDirectory() {
        return System.getProperty(JBOSS_CONFIG_DIR_PROPERTY);
    }

    @Override
    public boolean providerAvailable() {
        return System.getProperty(JBOSS_CONFIG_DIR_PROPERTY) != null;
    }
}
