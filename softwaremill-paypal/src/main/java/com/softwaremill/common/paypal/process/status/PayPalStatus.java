package pl.softwaremill.common.paypal.process.status;

public class PayPalStatus {

    private String status;

    PayPalStatus(String status) {
        this.status = status;
    }

    public boolean isVerified() {
        return "VERIFIED".equals(status);
    }

    public boolean isValidCurrency(String paymentCurrency) {
        return "AUD".equals(paymentCurrency);
    }

    public boolean isInvalid() {
        return "INVALID".equals(status);
    }

    public boolean isUnknown() {
        return !isVerified() && !isInvalid();
    }

    @Override
    public String toString() {
        return "PayPalStatus{" +
                "status='" + status + '\'' +
                '}';
    }
}
