package com.softwaremill.common.paypal.process;

public class PayPalParameters {

    private static final String PAYPAL_CREDIT_PREFIX = "paypal-";
    private static final String PAYPALREFUND_CREDIT_PREFIX = "paypalrefund-";

    private String itemName;
    private String itemNumber;
    private String paymentStatus;
    private String paymentAmount;
    private String paymentCurrency;
    private String txnId;
    private String receiverEmail;
    private String payerEmail;
    private String userId;
    private String option;
    private String parentTxnId;
    private String invoice;

    public static PayPalParameters create(RequestParameters requestParameters) {
        PayPalParameters parameters = new PayPalParameters();
        parameters.itemName = requestParameters.getParameter(RequestParameters.Parameter.item_name);
        parameters.itemNumber = requestParameters.getParameter(RequestParameters.Parameter.item_number);
        parameters.paymentStatus = requestParameters.getParameter(RequestParameters.Parameter.payment_status);
        parameters.paymentAmount = requestParameters.getParameter(RequestParameters.Parameter.mc_gross);
        parameters.paymentCurrency = requestParameters.getParameter(RequestParameters.Parameter.mc_currency);
        parameters.txnId = requestParameters.getParameter(RequestParameters.Parameter.txn_id);
        parameters.receiverEmail = requestParameters.getParameter(RequestParameters.Parameter.receiver_email);
        parameters.payerEmail = requestParameters.getParameter(RequestParameters.Parameter.payer_email);
        parameters.userId = requestParameters.getParameter(RequestParameters.Parameter.custom);
        parameters.option = requestParameters.getParameter(RequestParameters.Parameter.option_selection1);
        parameters.parentTxnId = requestParameters.getParameter(RequestParameters.Parameter.parent_txn_id);
        parameters.invoice = requestParameters.getParameter(RequestParameters.Parameter.invoice);
        return parameters;
    }

    private PayPalParameters() {
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemNumber() {
        return itemNumber;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public String getPaymentAmount() {
        return paymentAmount;
    }

    public String getPaymentCurrency() {
        return paymentCurrency;
    }

    public String getTxnId() {
        return txnId;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public String getPayerEmail() {
        return payerEmail;
    }

    public String getUserId() {
        return userId;
    }

    public String getOption() {
        return option;
    }

    public String getParentTxnId() {
        return parentTxnId;
    }

    public boolean isCompleted() {
        return "Completed".equals(paymentStatus);
    }

    public String getPaymentId() {
        return PAYPAL_CREDIT_PREFIX + txnId;
    }

    public boolean isRefunded() {
        return "Refunded".equals(paymentStatus);
    }

    public String getRefundPaymentId() {
        return PAYPALREFUND_CREDIT_PREFIX + txnId;
    }

    public String getParentPaymentId() {
        return PAYPAL_CREDIT_PREFIX + parentTxnId;
    }

    public String getInvoice() {
        return invoice;
    }

    @Override
    public String toString() {
        return "PayPalParameters{" +
                "itemName='" + itemName + '\'' +
                ", itemNumber='" + itemNumber + '\'' +
                ", paymentStatus='" + paymentStatus + '\'' +
                ", paymentAmount='" + paymentAmount + '\'' +
                ", paymentCurrency='" + paymentCurrency + '\'' +
                ", txnId='" + txnId + '\'' +
                ", receiverEmail='" + receiverEmail + '\'' +
                ", payerEmail='" + payerEmail + '\'' +
                ", userId='" + userId + '\'' +
                ", option='" + option + '\'' +
                ", invoice='" + invoice + '\'' +
                '}';
    }


}
