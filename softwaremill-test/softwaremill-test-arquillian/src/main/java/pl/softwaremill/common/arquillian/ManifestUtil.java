package pl.softwaremill.common.arquillian;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.container.ManifestContainer;

public class ManifestUtil {
    public static <T extends Archive<T> & ManifestContainer<T>> T addEmptyBeansXml(T archive) {
        return archive.addManifestResource(
                new ByteArrayAsset("<beans/>".getBytes()),
                ArchivePaths.create("META-INF/beans.xml"));
    }
}
