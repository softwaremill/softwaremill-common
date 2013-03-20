package com.softwaremill.common.conf;

/**
 * JBoss 7 configuration provider
 */
public class JBoss7DeployPropertiesProvider extends FolderPropertiesProvider {
    public static final String JBOSS_CONFIG_DIR_PROPERTY = "jboss.server.config.dir";

    @Override
    public String lookupConfigDirectory() {
        return "file:///" + System.getProperty(JBOSS_CONFIG_DIR_PROPERTY);
    }

    @Override
    public boolean providerAvailable() {
        return System.getProperty(JBOSS_CONFIG_DIR_PROPERTY) != null;
    }
}
