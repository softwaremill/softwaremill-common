package pl.softwaremill.common.conf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class Configuration {
    private static final Logger log = LoggerFactory.getLogger(Configuration.class);

    private static Map<String, Properties> configurations = new ConcurrentHashMap<String, Properties>();
    private static List<PropertiesProvider> propertyProviders = new ArrayList<PropertiesProvider>();

    /**
     * @param name Name of the configuration.
     * @return The properties of the given configuration.
     */
    public static Properties get(String name) {
        if (configurations.containsKey(name)) {
            return configurations.get(name);
        }

        for (PropertiesProvider propertyProvider : propertyProviders) {
            Properties props = propertyProvider.lookupProperties(name);
            if (props != null) {
                log.info("Loaded configuration for: " + name + " using " + propertyProvider.getClass().getName());
                configurations.put(name, props);
                return props;
            }
        }

        throw new RuntimeException("No configuration found for: " + name);
    }

    public static void registerPropertiesProvider(PropertiesProvider provider) {
        propertyProviders.add(provider);
    }

    // Registering default property providers. First JBoss, then classpath.
    static {
        registerPropertiesProvider(new JBossDeployPropertiesProvider());
        registerPropertiesProvider(new ClasspathPropertiesProvider());
    }

    static Properties loadFromURL(URL url) {
        Properties props = new Properties();
        try {
            InputStream is = url.openStream();
            try {
                props.load(is);
            } finally {
                is.close();
            }

            return props;
        } catch (IOException e) {
            log.error("Error while reading configuration from url: " + url.toString(), e);
            return null;
        }
    }
}
