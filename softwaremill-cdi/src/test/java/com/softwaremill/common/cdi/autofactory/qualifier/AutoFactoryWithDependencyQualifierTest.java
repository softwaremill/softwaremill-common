package com.softwaremill.common.cdi.autofactory.qualifier;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.testng.annotations.Test;
import com.softwaremill.common.arquillian.ManifestUtil;
import com.softwaremill.common.cdi.autofactory.CreatedWith;
import com.softwaremill.common.cdi.autofactory.PriceCalculator;
import com.softwaremill.common.cdi.autofactory.Product;

import javax.inject.Inject;

import static org.fest.assertions.Assertions.*;


public class AutoFactoryWithDependencyQualifierTest extends Arquillian {
    @Deployment
    public static JavaArchive createTestArchive() {
        JavaArchive ar = ShrinkWrap.create(JavaArchive.class, "test.jar")
                .addClass(QualifiedDependency.class)
                .addClass(PriceCalculatorQualifiedDependencyImpl.class)
                .addPackage(CreatedWith.class.getPackage());

        return ManifestUtil.addEmptyBeansXml(ar);
    }

    @Inject 
    private PriceCalculator.Factory priceCalculatorFactory;

    @Test
    public void testExamplePriceCalculator() {
        assertThat(priceCalculatorFactory.create(new Product(12)).getFinalPrice()).isEqualTo(11);
    }
}
