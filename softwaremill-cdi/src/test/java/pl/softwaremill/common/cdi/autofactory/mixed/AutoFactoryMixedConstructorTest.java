package pl.softwaremill.common.cdi.autofactory.mixed;

import org.jboss.arquillian.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import pl.softwaremill.common.cdi.autofactory.AbstractAutoFactoryTest;
import pl.softwaremill.common.cdi.autofactory.CreatedWith;
import pl.softwaremill.common.cdi.util.ArquillianUtil;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class AutoFactoryMixedConstructorTest extends AbstractAutoFactoryTest {
    @Deployment
    public static JavaArchive createTestArchive() {
        JavaArchive ar = ShrinkWrap.create(JavaArchive.class, "test.jar")
                .addClass(PriceCalculatorMixedConstructorImpl.class)
                .addClass(TotalPriceCalculatorMixedConstructorImpl.class)
                .addPackage(CreatedWith.class.getPackage());

        return ArquillianUtil.addEmptyBeansXml(ar);
    }
}
