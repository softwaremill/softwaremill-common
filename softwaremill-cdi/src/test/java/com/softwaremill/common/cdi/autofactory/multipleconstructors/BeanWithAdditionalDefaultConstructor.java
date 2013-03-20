package com.softwaremill.common.cdi.autofactory.multipleconstructors;

import com.softwaremill.common.cdi.autofactory.CreatedWith;
import com.softwaremill.common.cdi.autofactory.Discounts;
import com.softwaremill.common.cdi.autofactory.FactoryParameter;
import com.softwaremill.common.cdi.autofactory.Product;

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
