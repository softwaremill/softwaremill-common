package pl.softwaremill.common.cdi.autofactory.mixed;

import pl.softwaremill.common.cdi.autofactory.*;

import javax.inject.Inject;
import java.util.List;

/**
 * @author Adam Warski (adam at warski dot org)
 */
@CreatedWith(TotalPriceCalculator.Factory.class)
public class TotalPriceCalculatorMixedConstructorImpl implements TotalPriceCalculator {
    private final List<Product> products;
    private final int specialDiscount;
    private final PriceCalculator.Factory priceCalculatorFactory;
    private final BulkOrderDiscounts bulkOrderDiscounts;

    @Inject
    public TotalPriceCalculatorMixedConstructorImpl(@FactoryParameter List<Product> products,
                                                    @FactoryParameter int specialDiscount,
                                                    PriceCalculator.Factory priceCalculatorFactory,
                                                    BulkOrderDiscounts bulkOrderDiscounts) {
        this.products = products;
        this.specialDiscount = specialDiscount;
        this.priceCalculatorFactory = priceCalculatorFactory;
        this.bulkOrderDiscounts = bulkOrderDiscounts;
    }

    @Override
    public int getTotalPrice() {
        int sum = 0;
        for (Product product : products) {
            sum += priceCalculatorFactory.create(product).getFinalPrice();
        }

        return sum - specialDiscount - bulkOrderDiscounts.getDiscountForBulkOrderSize(products.size());
    }
}
