package com.softwaremill.common.cdi.autofactory.producer;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.testng.annotations.Test;
import com.softwaremill.common.arquillian.ManifestUtil;
import com.softwaremill.common.cdi.autofactory.AbstractAutoFactoryTest;
import com.softwaremill.common.cdi.autofactory.CreatedWith;
import com.softwaremill.common.cdi.autofactory.PriceCalculator;
import com.softwaremill.common.cdi.autofactory.mixed.PriceCalculatorMixedConstructorImpl;
import com.softwaremill.common.cdi.autofactory.mixed.TotalPriceCalculatorMixedConstructorImpl;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

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

        return ManifestUtil.addEmptyBeansXml(ar);
    }

    @Inject @ExampleProductQualifier
    private PriceCalculator examplePriceCalculator;

    @Test
    public void testExamplePriceCalculator() {
        assertThat(examplePriceCalculator.getFinalPrice()).isEqualTo(-10);
    }
}
