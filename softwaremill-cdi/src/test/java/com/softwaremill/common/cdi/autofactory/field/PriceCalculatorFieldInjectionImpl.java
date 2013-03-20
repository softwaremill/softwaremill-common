package pl.softwaremill.common.cdi.autofactory.field;

import pl.softwaremill.common.cdi.autofactory.CreatedWith;
import pl.softwaremill.common.cdi.autofactory.Discounts;
import pl.softwaremill.common.cdi.autofactory.PriceCalculator;
import pl.softwaremill.common.cdi.autofactory.Product;

import javax.inject.Inject;

/**
 * @author Adam Warski (adam at warski dot org)
 */
@CreatedWith(PriceCalculator.Factory.class)
public class PriceCalculatorFieldInjectionImpl implements PriceCalculator {
    @Inject
    private Discounts discounts;

    private final Product product;

    public PriceCalculatorFieldInjectionImpl(Product product) {
        this.product = product;
    }

    @Override
    public int getFinalPrice() {
        return product.getBasePrice() - discounts.getNormalDiscount();
    }
}
