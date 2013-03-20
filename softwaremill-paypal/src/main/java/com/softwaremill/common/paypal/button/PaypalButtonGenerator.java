package com.softwaremill.common.paypal.button;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The class takes cart items and generates paypal button/form that is used on the webpage
 */
public class PaypalButtonGenerator {

    private String sellerPaypalEmail;
    private boolean isSandbox;
    private String currency;

    private String defaultShipping = "0.0";
    private String defaultTax = "0.0";

    private String paypalButtonLabel = "Pay with PayPal";

    private String paypalButtonImage = null;

    private String invoiceNumber = null;

    private String notifyUrl = null;

    private String goBackLink;
    private String goBackCancelLink;

    private List<PaypalCartItem> cartItems = new ArrayList<PaypalCartItem>();

    public PaypalButtonGenerator(String sellerPaypalEmail, String goBackLink, String goBackCancelLink) {
        this(sellerPaypalEmail, goBackLink, goBackCancelLink, false, "USD");
    }

    public PaypalButtonGenerator(String sellerPaypalEmail, String goBackLink, String goBackCancelLink,
                                 boolean sandbox, String currency) {
        this.sellerPaypalEmail = sellerPaypalEmail;
        isSandbox = sandbox;
        this.currency = currency;
        this.goBackLink = goBackLink;
        this.goBackCancelLink = goBackCancelLink;
    }

    public PaypalButtonGenerator withDefaultShipping(String defaultShipping) {
        this.defaultShipping = defaultShipping;
        return this;
    }

    public PaypalButtonGenerator withDefaultTax(String defaultTax) {
        this.defaultTax = defaultTax;
        return this;
    }

    public PaypalButtonGenerator withPaypalButtonLabel(String paypalButtonLabel) {
        this.paypalButtonLabel = paypalButtonLabel;
        return this;
    }

    public PaypalButtonGenerator withCurrency(String currency) {
        this.currency = currency;
        return this;
    }

    public PaypalButtonGenerator withSubmitImage(String imageSrc) {
        paypalButtonImage = imageSrc;
        return this;
    }

    public PaypalButtonGenerator withInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
        return this;
    }

    public PaypalButtonGenerator withNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
        return this;
    }

    public PaypalButtonGenerator addItem(String name, String amount, String shipping, String tax) {
        cartItems.add(new PaypalCartItem(name, amount, shipping, tax));
        return this;
    }

    public PaypalButtonGenerator addItemWithTax(String name, String amount, String tax) {
        cartItems.add(new PaypalCartItem(name, amount, defaultShipping, tax));
        return this;
    }

    public PaypalButtonGenerator addItemWithShipping(String name, String amount, String shipping) {
        cartItems.add(new PaypalCartItem(name, amount, shipping, defaultTax));
        return this;
    }

    public PaypalButtonGenerator addItem(String name, String amount) {
        cartItems.add(new PaypalCartItem(name, amount, defaultShipping, defaultTax));
        return this;
    }

    public PaypalButtonGenerator addItems(PaypalCartItem... items) {
        cartItems.addAll(Arrays.asList(items));
        return this;
    }

    public String build() {
        StringBuffer sb = new StringBuffer();

        if (cartItems.isEmpty()) {
            // there's nothing to pay for, return disabled button

            sb.append("<form><input type='submit' value='")
                    .append(paypalButtonLabel)
                    .append("' disabled='true'/></form>\n");
        }


        // start with the form header
        sb.append("<form action=\"https://www.");
        if (isSandbox) {
            sb.append("sandbox.");
        }
        sb.append("paypal.com/cgi-bin/webscr\" method=\"post\">\n");

        // cart command so paypal knows what is it
        sb.append("\t<input type=\"hidden\" name=\"cmd\" value=\"_cart\"/>\n");
        sb.append("\t<input type=\"hidden\" name=\"upload\" value=\"1\"/>\n");

        // where should the user be taken after clicking continue shopping
        sb.append("\t<input type=\"hidden\" name=\"return\" value=\"").append(goBackLink).append("\"/>\n");
        sb.append("\t<input type=\"hidden\" name=\"cancel_return\" value=\"").append(goBackCancelLink).append("\"/>\n");
        // this will access cancel/return links via get
        sb.append("\t<input type=\"hidden\" name=\"rm\" value=\"1\"/>\n");

        // our seller email
        sb.append("\t<input type=\"hidden\" name=\"business\" value=\"").append(sellerPaypalEmail).append("\"/>\n");

        // the currency
        sb.append("\t<input type=\"hidden\" name=\"currency_code\" value=\"").append(currency).append("\"/>\n");

        // the cart items
        for (int i = 1; i <= cartItems.size(); i++) {
            PaypalCartItem cartItem = cartItems.get(i - 1);

            sb.append("\t<input type=\"hidden\" name=\"item_name_").append(i).append("\" value=\"")
                    .append(cartItem.getItemName()).append("\"/>\n");
            sb.append("\t<input type=\"hidden\" name=\"amount_").append(i).append("\" value=\"")
                    .append(cartItem.getItemAmount()).append("\"/>\n");
            sb.append("\t<input type=\"hidden\" name=\"shipping_").append(i).append("\" value=\"")
                    .append(cartItem.getItemShippingAmount()).append("\"/>\n");
            sb.append("\t<input type=\"hidden\" name=\"tax_").append(i).append("\" value=\"")
                    .append(cartItem.getItemTaxAmount()).append("\"/>\n");
        }

        // optional invoice number
        if (invoiceNumber != null) {
            sb.append("\t<input type=\"hidden\" name=\"invoice\" value=\"")
                    .append(invoiceNumber).append("\"/>\n");
        }

        // optional notify url
        if (notifyUrl != null) {
            sb.append("\t<input type=\"hidden\" name=\"notify_url\" value=\"")
                    .append(notifyUrl).append("\"/>\n");
        }

        // add submit button
        if (paypalButtonImage != null) {
            // use image
            sb.append("\t<input type=\"image\" alt=\"").append(paypalButtonLabel)
                    .append("\" src=\"").append(paypalButtonImage).append("\"/>\n");
        }
        else {
            sb.append("\t<input type=\"submit\" value=\"").append(paypalButtonLabel).append("\"/>\n");
        }

        // and close the form
        sb.append("</form>\n");

        return sb.toString();
    }
}
