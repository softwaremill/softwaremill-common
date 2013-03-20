package com.softwaremill.common.conf;

import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Common logic for all folder-based providers
 *
 * @author Adam Warski (adam at warski dot org)
 */
public abstract class FolderPropertiesProvider implements PropertiesProvider {
    private static final Logger log = LoggerFactory.getLogger(FolderPropertiesProvider.class);

    @Override
    public ImmutableMap<String, String> lookupProperties(String name) {
        // Looking up the server's conf directory
        String serverConfDirectory = lookupConfigDirectory();

        if (serverConfDirectory == null) {
            return null;
        }

        // make sure it ends with /
        if (!serverConfDirectory.endsWith(File.separator)) {
            serverConfDirectory = serverConfDirectory + File.separator;
        }

        // Reading the file
        try {
            URL fileURL = new URL(serverConfDirectory + name + ".conf");
            return Configuration.loadFromURL(fileURL);
        } catch (MalformedURLException e) {
            log.error("Error while reading properties from jboss: " + name, e);
            return null;
        }
    }

    public abstract String lookupConfigDirectory();
}
