package pl.softwaremill.common.cdi.util;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.container.ManifestContainer;
import org.jboss.shrinkwrap.api.container.ResourceContainer;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class ArquillianUtil {
    public static <T extends Archive<T> & ManifestContainer<T>> T addEmptyBeansXml(T archive) {
        return archive.addManifestResource(
                        new ByteArrayAsset("<beans/>".getBytes()),
                        ArchivePaths.create("META-INF/beans.xml"));
    }

    public static <T extends Archive<T> & ResourceContainer<T>> T addTestBeansXml(T archive) {
        StringBuilder beansXmlBuilder = new StringBuilder();
        beansXmlBuilder.append("<beans>");
        beansXmlBuilder.append("<interceptors>");
        beansXmlBuilder.append("<class>pl.softwaremill.common.cdi.security.SecurityInterceptor</class>");
        beansXmlBuilder.append("<class>pl.softwaremill.common.cdi.security.SecurityResultInterceptor</class>");
        beansXmlBuilder.append("</interceptors>");
        beansXmlBuilder.append("</beans>");

        return archive.addResource(
                new ByteArrayAsset(beansXmlBuilder.toString().getBytes()),
                ArchivePaths.create("META-INF/beans.xml"));
    }

    public static <T extends Archive<T> & ResourceContainer<T>> T addExtensionsFromApp(T archive) {
        StringBuilder extensionsBuilder = new StringBuilder();
        extensionsBuilder.append("pl.softwaremill.common.cdi.security.SecurityExtension");
        extensionsBuilder.append("pl.softwaremill.common.cdi.config.ConfigExtension");
        extensionsBuilder.append("pl.softwaremill.common.cdi.objectservice.extension.ObjectServiceExtension");

        return archive.addResource(
                new ByteArrayAsset(extensionsBuilder.toString().getBytes()),
                ArchivePaths.create("META-INF/services/javax.enterprise.inject.spi.Extension"));
    }
}
