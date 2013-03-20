package com.softwaremill.common.dbtest;

import com.google.common.base.Charsets;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import com.softwaremill.common.cdi.persistence.EntityWriter;
import com.softwaremill.common.cdi.transaction.TransactionalInterceptor;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public abstract class ArchiveConfigurator {
    public JavaArchive createTestArchive() {
        JavaArchive ar = configureBeans(ShrinkWrap.create(JavaArchive.class))
                .addPackage(EntityWriter.class.getPackage())
                .addPackage(TransactionalInterceptor.class.getPackage());

        // Creating beans.xml
        StringBuilder beansXmlBuilder = new StringBuilder();
        beansXmlBuilder.append("<beans>");
        fillBeansXml(beansXmlBuilder);
        beansXmlBuilder.append("</beans>");

        ar = ar.addAsManifestResource(
                new ByteArrayAsset(beansXmlBuilder.toString().getBytes(Charsets.UTF_8)),
                ArchivePaths.create("beans.xml"));

        return ar;
    }

    /**
     * Implement this and add beans that are tested in the test and which should be deployed.
     */
    protected abstract JavaArchive configureBeans(JavaArchive ar);

    /**
     * Overwrite this to add custom configuration to beans.xml.
     */
    protected void fillBeansXml(StringBuilder sb) {
    }
}
