package com.softwaremill.common.cdi.objectservice.auto;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.testng.annotations.Test;
import com.softwaremill.common.arquillian.ManifestUtil;

import javax.inject.Inject;
import java.net.MalformedURLException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for auto object services
 */
@Test(groups = "autoObjectGroup", dependsOnGroups = "basicObjectGroup")
public class AutoServiceTest extends Arquillian {

    @Deployment
    public static JavaArchive createTestArchive() {
        JavaArchive ar = ShrinkWrap.create(JavaArchive.class, "test.jar")
                .addPackage(IAuto.class.getPackage())
                .addPackage(AutoObjectServiceExtension.class.getPackage());

        ar = ManifestUtil.addEmptyBeansXml(ar);

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
        assertThat(execMock.myStringExecs).isEqualTo(0);

        auto.doSomething("", new String("http://softwaremill.pl"), 1);

        assertThat(execMock.stringExecs).isEqualTo(1);
        assertThat(execMock.myStringExecs).isEqualTo(0);

        auto.doSomething("", new MyString("http://softwaremill.pl"), 1);

        assertThat(execMock.stringExecs).isEqualTo(1);
        assertThat(execMock.myStringExecs).isEqualTo(1);
    }

    @Test(dependsOnMethods = "testAutoWire")
    public void testScopes() {
        assertThat(autoString.getInvCounter()).isEqualTo(1);

        // make few new calls
        auto.doSomething("", "", 1);
        auto.doSomething("", "", 1);

        assertThat(autoString.getInvCounter()).isEqualTo(3);
    }

    @Test(expectedExceptions = {AutoOSException.class},
          expectedExceptionsMessageRegExp = "Cannot resolve implementation of @OS.*")
    public void testCallWithWrongObject() {
        auto.doSomething("", new Integer(0), 1);
    }

    @Test
    public void testInheritanceCalls() {
        int myStringExecs = execMock.myStringExecs;
        int stringExecs = execMock.stringExecs;

        auto.doSomething("", new MySpecialString("simple"), 1);

        assertThat(execMock.myStringExecs).isEqualTo(myStringExecs + 1);
        assertThat(execMock.stringExecs).isEqualTo(stringExecs);
    }
}
