package com.softwaremill.common.cdi.autofactory.mixed;

import com.softwaremill.common.cdi.autofactory.*;

import javax.inject.Inject;

/**
 * @author Adam Warski (adam at warski dot org)
 */
@CreatedWith(PriceCalculator.Factory.class)
public class PriceCalculatorMixedConstructorImpl implements PriceCalculator {
    private final Product product;
    private final Discounts discounts;

    @Inject
    public PriceCalculatorMixedConstructorImpl(@FactoryParameter Product product, Discounts discounts) {
        this.product = product;
        this.discounts = discounts;
    }

    @Override
    public int getFinalPrice() {
        return product.getBasePrice() - discounts.getNormalDiscount();
    }
}
