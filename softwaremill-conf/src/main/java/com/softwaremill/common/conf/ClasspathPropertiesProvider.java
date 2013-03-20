package com.softwaremill.common.conf;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;

import java.net.URL;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class ClasspathPropertiesProvider implements PropertiesProvider {
    @Override
    public ImmutableMap<String, String> lookupProperties(String name) {
        try {
            URL resource = Resources.getResource(name + ".conf");
            return Configuration.loadFromURL(resource);
        } catch (IllegalArgumentException e) {
            // Resource not found.
            return null;
        }
    }

    @Override
    public boolean providerAvailable() {
        return true;
    }
}

