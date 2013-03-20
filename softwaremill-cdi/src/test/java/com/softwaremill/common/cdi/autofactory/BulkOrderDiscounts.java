package com.softwaremill.common.cdi.autofactory;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class BulkOrderDiscounts {
    public int getDiscountForBulkOrderSize(int size) {
        return size/2;
    }
}
