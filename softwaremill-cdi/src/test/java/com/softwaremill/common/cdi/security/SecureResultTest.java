package com.softwaremill.common.cdi.security;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.testng.annotations.Test;
import com.softwaremill.common.cdi.el.ELEvaluator;
import com.softwaremill.common.cdi.util.ArquillianUtil;

import javax.inject.Inject;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class SecureResultTest extends Arquillian {
    @Deployment
    public static JavaArchive createTestArchive() {
        JavaArchive ar = ShrinkWrap.create(JavaArchive.class, "test.jar")
                .addPackage(SecureResultTest.class.getPackage())
                .addPackage(SecureResult.class.getPackage())
                .addPackage(ELEvaluator.class.getPackage());

        ar = ArquillianUtil.addTestBeansXml(ar);

        return ar;
    }

    @Inject
    private SecureResultBean secureResultBean;

    @Test
    public void testDirectSecurePass() {
        secureResultBean.method1("a");
    }

    @Test(expectedExceptions = SecurityConditionException.class)
    public void testDirectSecureFail() {
        secureResultBean.method1("b");
    }
}
