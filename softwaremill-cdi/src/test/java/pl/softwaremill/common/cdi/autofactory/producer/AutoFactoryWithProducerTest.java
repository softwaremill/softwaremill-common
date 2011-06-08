package pl.softwaremill.common.cdi.autofactory.producer;

import org.jboss.arquillian.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.testng.annotations.Test;
import pl.softwaremill.common.cdi.autofactory.AbstractAutoFactoryTest;
import pl.softwaremill.common.cdi.autofactory.CreatedWith;
import pl.softwaremill.common.cdi.autofactory.PriceCalculator;
import pl.softwaremill.common.cdi.autofactory.mixed.PriceCalculatorMixedConstructorImpl;
import pl.softwaremill.common.cdi.autofactory.mixed.TotalPriceCalculatorMixedConstructorImpl;
import pl.softwaremill.common.cdi.util.ArquillianUtil;

import javax.inject.Inject;

import static org.fest.assertions.Assertions.*;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class AutoFactoryWithProducerTest extends AbstractAutoFactoryTest {
    @Deployment
    public static JavaArchive createTestArchive() {
        JavaArchive ar = ShrinkWrap.create(JavaArchive.class, "test.jar")
                .addClass(PriceCalculatorMixedConstructorImpl.class)
                .addClass(TotalPriceCalculatorMixedConstructorImpl.class)
                .addClass(ExampleProductQualifier.class)
                .addClass(ExamplePriceCalculatorProducer.class)
                .addPackage(CreatedWith.class.getPackage());

        return ArquillianUtil.addEmptyBeansXml(ar);
    }

    @Inject @ExampleProductQualifier
    private PriceCalculator examplePriceCalculator;

    @Test
    public void testExamplePriceCalculator() {
        assertThat(examplePriceCalculator.getFinalPrice()).isEqualTo(-10);
    }
}
