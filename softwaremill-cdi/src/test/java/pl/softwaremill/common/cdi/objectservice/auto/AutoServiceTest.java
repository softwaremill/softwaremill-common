package pl.softwaremill.common.cdi.objectservice.auto;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.testng.annotations.Test;
import pl.softwaremill.common.cdi.objectservice.OS;
import pl.softwaremill.common.cdi.objectservice.auto.IAuto;
import pl.softwaremill.common.cdi.objectservice.extension.ObjectServiceExtension;
import pl.softwaremill.common.cdi.util.ArquillianUtil;

import javax.inject.Inject;
import java.net.MalformedURLException;
import java.net.URL;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Test for auto object services
 */
public class AutoServiceTest extends Arquillian {

    @Deployment
    public static JavaArchive createTestArchive() {
        JavaArchive ar = ShrinkWrap.create(JavaArchive.class, "test.jar")
                .addPackage(IAuto.class.getPackage())
                .addPackage(AutoObjectServiceExtension.class.getPackage());

        ar = ArquillianUtil.addEmptyBeansXml(ar);

        return ar;
    }

    @Inject
    private IAuto auto;

    @Inject @OSImpl
    private AutoString autoString;

    @Inject
    private ExecutionMock execMock;

    @Test
    public void testAutoWire() throws MalformedURLException {

        assertThat(execMock.stringExecs).isEqualTo(0);
        assertThat(execMock.urlExecs).isEqualTo(0);

        auto.doSomething("", new String("http://softwaremill.pl"), 1);

        assertThat(execMock.stringExecs).isEqualTo(1);
        assertThat(execMock.urlExecs).isEqualTo(0);

        auto.doSomething("", new URL("http://softwaremill.pl"), 1);

        assertThat(execMock.stringExecs).isEqualTo(1);
        assertThat(execMock.urlExecs).isEqualTo(1);
    }

    @Test(dependsOnMethods = "testAutoWire")
    public void testScopes() {
        assertThat(autoString.getInvCounter()).isEqualTo(1);

        // make few new calls
        auto.doSomething("", "", 1);
        auto.doSomething("", "", 1);

        assertThat(autoString.getInvCounter()).isEqualTo(3);
    }
}
