package pl.softwaremill.common.arquillian;

import com.google.common.base.Charsets;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.container.ManifestContainer;

public class ManifestUtil {
    public static <T extends Archive<T> & ManifestContainer<T>> T addEmptyBeansXml(T archive) {
        return archive.addAsManifestResource(
                new ByteArrayAsset("<beans/>".getBytes(Charsets.UTF_8)),
                ArchivePaths.create("META-INF/beans.xml"));
    }
}
