package pl.softwaremill.common.conf;

import org.jboss.mx.util.MBeanServerLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

/**
 * JBoss 6 configuration provider
 */
public class JBoss6DeployPropertiesProvider extends FolderPropertiesProvider {

    private static final Logger log = LoggerFactory.getLogger(JBoss6DeployPropertiesProvider.class);

    @Override
    public String lookupConfigDirectory() {
        try {
            MBeanServer server = MBeanServerLocator.locate();
            return server.getAttribute(new ObjectName("jboss.system:type=ServerConfig"), "ServerConfLocation").toString();
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
    }

    @Override
    public boolean providerAvailable() {
        try {
            Thread.currentThread().getContextClassLoader().loadClass("org.jboss.mx.util.MBeanServerLocator");

            return true;
        } catch (ClassNotFoundException e) {
            // Class not found - not registering.
            return false;
        }
    }
}
