package pl.softwaremill.common.conf;

import com.google.common.collect.ImmutableMap;
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

    private static Map<String, Map<String, String>> configurations = new ConcurrentHashMap<String, Map<String, String>>();
    private static List<PropertiesProvider> propertyProviders = new ArrayList<PropertiesProvider>();

    /**
     * Reads configuration for the specified name.
     *
     * Will look for a {@code &lt;name&gt;.conf} file in two places:
     * (1) the jboss conf directory: {@code jboss/server/<current jboss conf>/conf}, if jboss is available
     * (2) the classpath
     *
     * The {@code &lt;name&gt;.conf} should be a simple key-value file.
     *
     * @param name Name of the configuration.
     * @return The properties of the given configuration.
     */
    public static Map<String, String> get(String name) {
        if (configurations.containsKey(name)) {
            return configurations.get(name);
        }

        for (PropertiesProvider propertyProvider : propertyProviders) {
            Map<String, String> props = propertyProvider.lookupProperties(name);
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

    // Registering default property providers. First JBoss (if available), then classpath.
    static {
        try {
            Thread.currentThread().getContextClassLoader().loadClass("org.jboss.mx.util.MBeanServerLocator");
            registerPropertiesProvider(new JBossDeployPropertiesProvider());
        } catch (ClassNotFoundException e) {
            // Class not found - not registering.
        }
        registerPropertiesProvider(new ClasspathPropertiesProvider());
    }

    static ImmutableMap<String, String> loadFromURL(URL url) {
        Properties props = new Properties();
        try {
            InputStream is = url.openStream();
            try {
                props.load(is);
            } finally {
                is.close();
            }

            ImmutableMap.Builder<String, String> propsAsImmMapBuilder = new ImmutableMap.Builder<String, String>();
            for (String propName : props.stringPropertyNames()) {
                propsAsImmMapBuilder = propsAsImmMapBuilder.put(propName, props.getProperty(propName));
            }

            return propsAsImmMapBuilder.build();
        } catch (IOException e) {
            log.error("Error while reading configuration from url: " + url.toString(), e);
            return null;
        }
    }
}
