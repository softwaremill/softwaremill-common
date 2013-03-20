package com.softwaremill.common.cdi.objectservice;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.softwaremill.common.arquillian.ManifestUtil;
import com.softwaremill.common.cdi.objectservice.extension.ObjectServiceExtension;

import javax.inject.Inject;

/**
 * @author Adam Warski (adam at warski dot org)
 */
@Test(groups = "basicObjectGroup")
public class BasicObjectServiceTest extends Arquillian {
    @Deployment
    public static JavaArchive createTestArchive() {
        JavaArchive ar = ShrinkWrap.create(JavaArchive.class, "test.jar")
                .addPackage(A.class.getPackage())
                .addPackage(OS.class.getPackage())
                .addPackage(ObjectServiceExtension.class.getPackage());

        ar = ManifestUtil.addEmptyBeansXml(ar);

        return ar;
    }

    @Inject
    private OSP<A, Service1<A>> service1;

    @Inject
    private OSP<A, Service2<A>> service2;

    @Inject
    private OSP<A, Service3<A>> service3;

    @Test
    public void testService1WithB() {
        Assert.assertEquals(service1.f(new B(10)).get(), 10);
    }

    @Test
    public void testService1WithC() {
        Assert.assertEquals(service1.f(new C("x")).get(), "x");
    }

    @Test
    public void testService1WithAnonymousB() {
        Assert.assertEquals(service1.f(new B(11) {
            @Override
            public Integer getValue() {
                return 12;
            }
        }).get(), 12);
    }

    @Test
    public void testNewServiceInstances2WithB() {
        // On each invocation of f() there should be a new bean created.
        int instancesStart = Service2B.instanceCount();
        Assert.assertEquals(service2.f(new B(10)).get(), 10);
        Assert.assertEquals(service2.f(new B(12)).get(), 12);
        int instancesEnd = Service2B.instanceCount();
        Assert.assertEquals(instancesEnd - instancesStart, 2);
    }

    @Test
    public void testNewServiceInstances2WithC() {
        // On each invocation of f() there should be a new bean created.
        int instancesStart = Service2C.instanceCount();
        Assert.assertEquals(service2.f(new C("x")).get(), "x");
        Assert.assertEquals(service2.f(new C("y")).get(), "y");
        int instancesEnd = Service2C.instanceCount();
        Assert.assertEquals(instancesEnd - instancesStart, 2);
    }

    @Test
    public void testService3WithB() {
        Assert.assertEquals(service3.f(new B(10)).get(), 10);
    }

    @Test
    public void testService3WithC() {
        Assert.assertEquals(service3.f(new C("x")).get(), "x");
    }
}
