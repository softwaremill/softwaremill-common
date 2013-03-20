package com.softwaremill.common.cdi.autofactory;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class Product {
    private int basePrice;

    public Product(int basePrice) {
        this.basePrice = basePrice;
    }

    public int getBasePrice() {
        return basePrice;
    }
}
