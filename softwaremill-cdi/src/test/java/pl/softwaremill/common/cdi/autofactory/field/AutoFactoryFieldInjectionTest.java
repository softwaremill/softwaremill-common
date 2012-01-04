package pl.softwaremill.common.cdi.autofactory.field;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import pl.softwaremill.common.arquillian.ManifestUtil;
import pl.softwaremill.common.cdi.autofactory.AbstractAutoFactoryTest;
import pl.softwaremill.common.cdi.autofactory.CreatedWith;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class AutoFactoryFieldInjectionTest extends AbstractAutoFactoryTest {
    @Deployment
    public static JavaArchive createTestArchive() {
        JavaArchive ar = ShrinkWrap.create(JavaArchive.class, "test.jar")
                .addClass(PriceCalculatorFieldInjectionImpl.class)
                .addClass(TotalPriceCalculatorFixedInjectionImpl.class)
                .addPackage(CreatedWith.class.getPackage());

        return ManifestUtil.addEmptyBeansXml(ar);
    }
}
