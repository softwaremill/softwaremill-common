package pl.softwaremill.common.paypal.process;

import javax.servlet.http.HttpServletRequest;

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

    public static PayPalParameters create(HttpServletRequest request) {
        PayPalParameters parameters = new PayPalParameters();
        parameters.itemName = request.getParameter("item_name");
        parameters.itemNumber = request.getParameter("item_number");
        parameters.paymentStatus = request.getParameter("payment_status");
        parameters.paymentAmount = request.getParameter("mc_gross");
        parameters.paymentCurrency = request.getParameter("mc_currency");
        parameters.txnId = request.getParameter("txn_id");
        parameters.receiverEmail = request.getParameter("receiver_email");
        parameters.payerEmail = request.getParameter("payer_email");
        parameters.userId = request.getParameter("custom");
        parameters.option = request.getParameter("option_selection1");
        parameters.parentTxnId = request.getParameter("parent_txn_id");
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
                '}';
    }
}
