package pl.softwaremill.common.cdi.autofactory;

import javax.inject.Inject;

/**
 * @author Adam Warski (adam at warski dot org)
 */
@CreatedWith(PriceCalculator.Factory.class)
public class PriceCalculatorImpl implements PriceCalculator {
    private final Product product;
    private final Discounts discounts;

    @Inject
    public PriceCalculatorImpl(@FactoryParameter Product product, Discounts discounts) {
        this.product = product;
        this.discounts = discounts;
    }

    @Override
    public int getFinalPrice() {
        return product.getBasePrice() - discounts.getNormalDiscount();
    }
}
