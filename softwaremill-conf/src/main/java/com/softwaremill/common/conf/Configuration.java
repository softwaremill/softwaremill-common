package pl.softwaremill.common.conf;

import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.softwaremill.common.conf.encoding.ConfigurationValueCoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
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

    private static Map<String, Config<String, String>> configurations =
            new ConcurrentHashMap<String, Config<String, String>>();
    private static List<PropertiesProvider> propertyProviders = new ArrayList<PropertiesProvider>();

    private static ConfigurationValueCoder valueCoder = new ConfigurationValueCoder();

    /**
     * Reads configuration for the specified name.
     *
     * Will look for a {@code &lt;name&gt;.conf} file in two places:
     * (1) the jboss conf directory: {@code jboss/server/<current jboss conf>/conf}, if jboss is available
     * (2) the classpath
     *
     * The {@code &lt;name&gt;.conf} should be a simple key-value file.
     *
     * @param name                       Name of the configuration.
     * @param readSystemPropertiesOnNull flag indicating if System.getProperty() should be called when configuration did not contain a key
     * @return The properties of the given configuration.
     */
    public static Config<String, String> get(String name, boolean readSystemPropertiesOnNull) {
        if (configurations.containsKey(name)) {
            return configurations.get(name);
        }

        for (PropertiesProvider propertyProvider : propertyProviders) {
            Map<String, String> props = propertyProvider.lookupProperties(name);
            if (props != null) {
                log.info("Loaded configuration for: " + name + " using " + propertyProvider.getClass().getName());
                Config<String, String> conf;
                if (readSystemPropertiesOnNull) {
                    conf = new SystemPropertiesMapWrapper(props);
                } else {
                    conf = new MapWrapper(props);
                }
                configurations.put(name, conf);
                return conf;
            }
        }

        throw new RuntimeException("No configuration found for: " + name);
    }

    public static Config<String, String> get(String name) {
        return get(name, false);
    }

    public static void registerPropertiesProvider(Class<? extends PropertiesProvider> providerClass) {
        PropertiesProvider provider;
        try {
            provider = providerClass.newInstance();
            registerPropertiesProvider(provider);
        } catch (InstantiationException e) {
            // do not register
        } catch (IllegalAccessException e) {
            // do not register
        }
    }

    public static void registerPropertiesProvider(PropertiesProvider provider) {
        if (provider.providerAvailable()) {
            propertyProviders.add(provider);
        }
    }

    public static void setConfiguration(String configurationName, ImmutableMap<String, String> properties) {
        log.info("Set configuration for: " + configurationName);
        configurations.put(configurationName, new MapWrapper(properties));
    }

    public static void clearConfiguration(String configurationName) {
        log.info("Cleared configuration for: " + configurationName);
        configurations.remove(configurationName);
    }

    public static void setFromFile(String configurationName, File file) throws MalformedURLException {
        ImmutableMap<String, String> conf = loadFromURL(file.toURI().toURL());
        setConfiguration(configurationName, conf);
    }

    // Registering default property providers. First JBoss (if available), then classpath.
    static {
        registerPropertiesProvider(JBoss5DeployPropertiesProvider.class);
        registerPropertiesProvider(JBoss6DeployPropertiesProvider.class);
        registerPropertiesProvider(JBoss7DeployPropertiesProvider.class);
        registerPropertiesProvider(ClasspathPropertiesProvider.class);
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
                String propValue = props.getProperty(propName);
                if (valueCoder.isEncoded(propValue)) {
                    propValue = valueCoder.decode(propValue);
                }

                propsAsImmMapBuilder = propsAsImmMapBuilder.put(propName, propValue);
            }

            return propsAsImmMapBuilder.build();
        } catch (FileNotFoundException e) {
            // Configuration not found in this provider - trying the next one.
            return null;
        } catch (IOException e) {
            log.error("Error while reading configuration from url: " + url.toString(), e);
            return null;
        }
    }
}
