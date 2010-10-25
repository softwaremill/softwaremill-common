package pl.softwaremill.common.cdi.autofactory;

import java.util.List;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public interface TotalPriceCalculator {
    int getTotalPrice();

    interface Factory {
        TotalPriceCalculator create(List<Product> products, int specialDiscount);
    }
}
