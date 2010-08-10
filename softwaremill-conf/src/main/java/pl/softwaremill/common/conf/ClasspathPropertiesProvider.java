package pl.softwaremill.common.conf;

import com.google.common.io.Resources;

import java.net.URL;
import java.util.Properties;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class ClasspathPropertiesProvider implements PropertiesProvider {
    @Override
    public Properties lookupProperties(String name) {
        try {
            URL resource = Resources.getResource(name + ".conf");
            return Configuration.loadFromURL(resource);
        } catch (IllegalArgumentException e) {
            // Resource not found.
            return null;
        }
    }
}

