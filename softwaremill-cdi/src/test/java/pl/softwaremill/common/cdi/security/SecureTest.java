package pl.softwaremill.common.cdi.security;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.testng.annotations.Test;
import pl.softwaremill.common.cdi.el.ELEvaluator;
import pl.softwaremill.common.cdi.util.ArquillianUtil;

import javax.inject.Inject;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class SecureTest extends Arquillian {
    @Deployment
    public static JavaArchive createTestArchive() {
        JavaArchive ar = ShrinkWrap.create(JavaArchive.class, "test.jar")
                .addPackage(SecureTest.class.getPackage())
                .addPackage(Secure.class.getPackage())
                .addPackage(ELEvaluator.class.getPackage());

        ar = ArquillianUtil.addTestBeansXml(ar);
        ar = ArquillianUtil.addExtensionsFromApp(ar);

        return ar;
    }

    @Inject
    private SecureBeanDirect secureBeanDirect;

    @Inject
    private SecureBeanGlobal secureBeanGlobal;

    @Inject
    private SecureBeanStacked secureBeanStacked;

    @Inject
    private SecureBeanSecureVar secureBeanSecureVar;

    @Inject
    private SecureBeanSecureVarExp secureBeanSecureVarExp;

    @Inject
    private SecureBeanCompoundContextual secureBeanCompoundContextual;

    @Test
    public void testDirectSecurePass() {
        secureBeanDirect.method1();
    }

    @Test(expectedExceptions = SecurityConditionException.class)
    public void testDirectSecureFail() {
        secureBeanDirect.method2();
    }

    @Test(expectedExceptions = SecurityConditionException.class)
    public void testGlobalSecureFail() {
        secureBeanGlobal.method1();
    }

    @Test
    public void testStackedSecurePass() {
        secureBeanStacked.method1();
    }

    @Test(expectedExceptions = SecurityConditionException.class)
    public void testStackedSecureFail() {
        secureBeanStacked.method2();
    }

    @Test
    public void testSecureVarPass() {
        secureBeanSecureVar.method1(true);
    }

    @Test(expectedExceptions = SecurityConditionException.class)
    public void testSecureVarFail() {
        secureBeanSecureVar.method1(false);
    }

    @Test
    public void testSecureCompoundContextualPass() {
        secureBeanCompoundContextual.method1(true, true, true);
    }

    @Test(expectedExceptions = SecurityConditionException.class)
    public void testSecureCompoundContextualFail1() {
        secureBeanCompoundContextual.method1(false, true, true);
    }

    @Test(expectedExceptions = SecurityConditionException.class)
    public void testSecureCompoundContextualFail2() {
        secureBeanCompoundContextual.method1(true, false, true);
    }

    @Test(expectedExceptions = SecurityConditionException.class)
    public void testSecureCompoundContextualFail3() {
        secureBeanCompoundContextual.method1(true, true, false);
    }

    @Test
    public void testSecureVarExpPass() {
        secureBeanSecureVarExp.method1(new StringHolder("a"));
    }

    @Test(expectedExceptions = SecurityConditionException.class)
    public void testSecureVarExpFail() {
        secureBeanSecureVarExp.method1(new StringHolder("b"));
    }
}
