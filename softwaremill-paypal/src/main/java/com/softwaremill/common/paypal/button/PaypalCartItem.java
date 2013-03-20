package com.softwaremill.common.paypal.button;

/**
 * Cart item, uploaded to paypal
 */
public class PaypalCartItem {

    private final String itemName;
    private final String itemAmount;
    private final String itemShippingAmount;
    private final String itemTaxAmount;

    public PaypalCartItem(String itemName, String itemAmount, String itemShippingAmount, String itemTaxAmount)
        throws NumberFormatException {

        this.itemName = itemName;
        this.itemAmount = itemAmount;
        this.itemShippingAmount = itemShippingAmount;
        this.itemTaxAmount = itemTaxAmount;

        checkAmount(itemAmount);
        checkAmount(itemShippingAmount);
        checkAmount(itemTaxAmount);
    }

    private void checkAmount(String amount) {
        new Double(amount);
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemAmount() {
        return itemAmount;
    }

    public String getItemShippingAmount() {
        return itemShippingAmount;
    }

    public String getItemTaxAmount() {
        return itemTaxAmount;
    }
}
