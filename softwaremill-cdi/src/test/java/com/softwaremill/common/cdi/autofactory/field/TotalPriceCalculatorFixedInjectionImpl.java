package pl.softwaremill.common.cdi.autofactory.field;

import pl.softwaremill.common.cdi.autofactory.*;

import javax.inject.Inject;
import java.util.List;

/**
 * @author Adam Warski (adam at warski dot org)
 */
@CreatedWith(TotalPriceCalculator.Factory.class)
public class TotalPriceCalculatorFixedInjectionImpl implements TotalPriceCalculator {
    @Inject
    private PriceCalculator.Factory priceCalculatorFactory;

    @Inject
    private BulkOrderDiscounts bulkOrderDiscounts;

    private final List<Product> products;
    private final int specialDiscount;

    public TotalPriceCalculatorFixedInjectionImpl(List<Product> products, int specialDiscount) {
        this.products = products;
        this.specialDiscount = specialDiscount;
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
