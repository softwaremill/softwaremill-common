package pl.softwaremill.common.cdi.autofactory.multipleconstructors;

import pl.softwaremill.common.cdi.autofactory.CreatedWith;
import pl.softwaremill.common.cdi.autofactory.Discounts;
import pl.softwaremill.common.cdi.autofactory.FactoryParameter;
import pl.softwaremill.common.cdi.autofactory.Product;

import javax.inject.Inject;

/**
 * @author Maciej Bilas
 * @since 16/4/12 12:40
 */
@CreatedWith(BeanWithAdditionalDefaultConstructor.Factory.class)
public class BeanWithAdditionalDefaultConstructor {

    private final Discounts discounts;
    private final Product product;

    public interface Factory {
        public BeanWithAdditionalDefaultConstructor create(Product product);
    }

    public BeanWithAdditionalDefaultConstructor() {
        discounts = null;
        product = null;
    }

    @Inject
    public BeanWithAdditionalDefaultConstructor(Discounts discounts, @FactoryParameter Product product) {
        this.discounts = discounts;
        this.product = product;
    }

}
