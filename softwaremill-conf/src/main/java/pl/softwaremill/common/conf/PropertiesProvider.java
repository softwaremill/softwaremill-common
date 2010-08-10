package pl.softwaremill.common.conf;

import java.util.Properties;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public interface PropertiesProvider {
    Properties lookupProperties(String name);
}
