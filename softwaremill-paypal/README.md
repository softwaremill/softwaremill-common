# Paypal Utilities

## IPN servlet

To enable your application supporting paypal requests, you need to extend the abstract

```java
pl.softwaremill.common.paypal.servlet.IPNServlet
```

And provide an implementation of two things - PayPalErrorHandler and an instance of PayPalProcessorsFactory that will
return your own implementation of VerifiedPayPalProcessor.

### PayPalErrorHandler

Is the code that will be executed if the processing of the payment fails for any reason.

It provides a handy ErrorMessage object to dump all gathered information to some log/email etc.

### VerifiedPayPalProcessor

Is the processor that will perform the logic, once the payment has been successfully authorized. If you are using CDI,
 then you will probably inject some CDI service from your application and use it.

## Custom cart paypal button

PaypalButtonGenerator is the fluent java html generator for the paypal custom cart buttons
(that means, you have the shopping cart implemented in your application and you need to generate a button that will pass
all the information about items, prices, taxes etc. to paypal and will take user to the paypal website to perform a payment).

To find out how it works, it's best to check out the PaypalButtonGeneratorTest but you can check out this short example:

```java
PaypalButtonGenerator pbg = new PaypalButtonGenerator(
    "paypal@foo.bar",                           // merchand paypal email
    "http://foo.bar/shop",                      // link to redirect after successful transaction
    "http://foo.bar/shop/paymentCancelled",     // link to redirect after cancelled transaction
    false,                                      // work in sandbox
    "USD",)                                     // currency
.withInvoiceNumber(invoiceId)                   // invoice ID that will be passed back to IPN
.withNotifyUrl("http://foo.bar/shop/paypal");   // the IPN url

for (Item item : items) {
    pbg.addItem(
        item.getDescription(),                  // description shown on paypal
        item.getAmount(),                       // basic price
        item.getShippingAmount(),               // shipping costs
        item.getVatAmount());                   // vat amount
}
```
