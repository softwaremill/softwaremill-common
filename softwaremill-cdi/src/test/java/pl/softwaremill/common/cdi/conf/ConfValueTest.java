package pl.softwaremill.common.cdi.conf;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.testng.annotations.Test;
import pl.softwaremill.common.arquillian.ManifestUtil;

import javax.inject.Inject;

import static org.fest.assertions.Assertions.*;

public class ConfValueTest extends Arquillian {
    @Deployment
    public static JavaArchive createTestArchive() {
        JavaArchive ar = ShrinkWrap.create(JavaArchive.class, "test.jar")
                .addPackage(ConfValueProducer.class.getPackage());

        ar = ManifestUtil.addEmptyBeansXml(ar);

        return ar;
    }
    
    @Inject
    @ConfValue(confName = "conftest", confKey = "key1")
    private String key1;

    @Inject
    @ConfValue(confName = "conftest", confKey = "key2")
    private String key2;
    
    @Test
    public void shouldInjectKey1() {
        assertThat(key1).isEqualTo("value1");
    }

    @Test
    public void shouldInjectKey2() {
        assertThat(key2).isEqualTo("value2");
    }
}
