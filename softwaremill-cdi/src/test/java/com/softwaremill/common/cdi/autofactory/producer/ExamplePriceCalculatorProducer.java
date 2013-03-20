package com.softwaremill.common.cdi.autofactory.producer;

import com.softwaremill.common.cdi.autofactory.PriceCalculator;
import com.softwaremill.common.cdi.autofactory.Product;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class ExamplePriceCalculatorProducer {
    private final PriceCalculator.Factory priceCalculatorFactory;

    @Inject
    public ExamplePriceCalculatorProducer(PriceCalculator.Factory priceCalculatorFactory) {
        this.priceCalculatorFactory = priceCalculatorFactory;
    }

    @Produces @ExampleProductQualifier
    public PriceCalculator create() {
        return priceCalculatorFactory.create(new Product(0));
    }
}
