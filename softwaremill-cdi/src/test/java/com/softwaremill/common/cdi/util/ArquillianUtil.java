package com.softwaremill.common.cdi.util;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.container.ResourceContainer;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class ArquillianUtil {
    public static <T extends Archive<T> & ResourceContainer<T>> T addTestBeansXml(T archive) {
        StringBuilder beansXmlBuilder = new StringBuilder();
        beansXmlBuilder.append("<beans>");
        beansXmlBuilder.append("<interceptors>");
        beansXmlBuilder.append("<class>com.softwaremill.common.cdi.security.SecurityInterceptor</class>");
        beansXmlBuilder.append("<class>com.softwaremill.common.cdi.security.SecurityResultInterceptor</class>");
        beansXmlBuilder.append("</interceptors>");
        beansXmlBuilder.append("</beans>");

        return archive.addAsResource(
                new ByteArrayAsset(beansXmlBuilder.toString().getBytes()),
                ArchivePaths.create("META-INF/beans.xml"));
    }
}
