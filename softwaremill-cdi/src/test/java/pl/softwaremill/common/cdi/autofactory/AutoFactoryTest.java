package pl.softwaremill.common.cdi.autofactory;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.testng.annotations.Test;
import pl.softwaremill.common.cdi.util.ArquillianUtil;

import javax.inject.Inject;
import java.util.Arrays;

import static org.fest.assertions.Assertions.*;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class AutoFactoryTest extends Arquillian {
    @Deployment
    public static JavaArchive createTestArchive() {
        JavaArchive ar = ShrinkWrap.create(JavaArchive.class, "test.jar")
                .addPackage(CreatedWith.class.getPackage());

        ar = ArquillianUtil.addEmptyBeansXml(ar);

        return ar;
    }

    @Inject
    private PriceCalculator.Factory priceCalculatorFactory;

    @Inject
    private TotalPriceCalculator.Factory totalPriceCalculatorFactory;

    @Test
    public void testInjectOneNormalBeanIntoFactory() {
        // Given
        Product product = new Product(35);
        PriceCalculator priceCalculator = priceCalculatorFactory.create(product);

        // When
        int finalPrice = priceCalculator.getFinalPrice();

        // Then
        assertThat(finalPrice).isEqualTo(25);
    }

    @Test
    public void testInjectFactoryAndBeanIntoFactory() {
        // Given
        Product product1 = new Product(13);
        Product product2 = new Product(17);
        Product product3 = new Product(19);
        TotalPriceCalculator totalPriceCalculator = totalPriceCalculatorFactory.create(Arrays.asList(product1, product2, product3), 1);

        // When
        int sum = totalPriceCalculator.getTotalPrice();

        // Then
        assertThat(sum).isEqualTo(17); // 13-10 + 17-10 + 19-10 - 1 - (3/2)
    }
}
