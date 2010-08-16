package pl.softwaremill.common.conf;

import com.google.common.collect.ImmutableMap;
import org.jboss.mx.util.MBeanServerLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class JBossDeployPropertiesProvider implements PropertiesProvider {
    private static final Logger log = LoggerFactory.getLogger(JBossDeployPropertiesProvider.class);

    @Override
    public ImmutableMap<String, String> lookupProperties(String name) {
        // Looking up the server's conf directory
        String serverConfDirectory;
        try {
            MBeanServer server = MBeanServerLocator.locate();
            serverConfDirectory = server.getAttribute(new ObjectName("jboss.system:type=ServerConfig"), "ServerConfLocation").toString();
        } catch (RuntimeException e) {
            return null;
        } catch (MBeanException e) {
            return null;
        } catch (AttributeNotFoundException e) {
            log.error("Cannot get server conf directory", e);
            return null;
        } catch (InstanceNotFoundException e) {
            log.error("Cannot get server conf directory", e);
            return null;
        } catch (ReflectionException e) {
            log.error("Cannot get server conf directory", e);
            return null;
        } catch (MalformedObjectNameException e) {
            log.error("Cannot get server conf directory", e);
            return null;
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
}
