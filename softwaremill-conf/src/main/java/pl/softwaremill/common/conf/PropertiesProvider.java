package pl.softwaremill.common.conf;

import com.google.common.collect.ImmutableMap;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public interface PropertiesProvider {
    ImmutableMap<String, String> lookupProperties(String name);
}
