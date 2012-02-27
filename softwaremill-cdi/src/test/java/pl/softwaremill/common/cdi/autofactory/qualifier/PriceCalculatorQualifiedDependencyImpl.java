package pl.softwaremill.common.cdi.autofactory.qualifier;

import pl.softwaremill.common.cdi.autofactory.CreatedWith;
import pl.softwaremill.common.cdi.autofactory.FactoryParameter;
import pl.softwaremill.common.cdi.autofactory.PriceCalculator;
import pl.softwaremill.common.cdi.autofactory.Product;

import javax.inject.Inject;

@CreatedWith(PriceCalculator.Factory.class)
public class PriceCalculatorQualifiedDependencyImpl implements PriceCalculator {
    private final Product product;
    private final QualifiedDependency qualifiedDependency;

    @Inject
    public PriceCalculatorQualifiedDependencyImpl(@FactoryParameter Product product,
                                                  @ExampleStringQualifier QualifiedDependency qualifiedDependency) {
        this.product = product;
        this.qualifiedDependency = qualifiedDependency;
    }

    @Override
    public int getFinalPrice() {
        return product.getBasePrice()-1;
    }
}
