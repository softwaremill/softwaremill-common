package pl.softwaremill.common.cdi.autofactory;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public interface PriceCalculator {
    int getFinalPrice();    

    interface Factory {
        PriceCalculator create(Product product);
    }
}
