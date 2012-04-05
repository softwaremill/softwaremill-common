package pl.softwaremill.common.paypal.process.status;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Read message from PayPal and add cmd with validate
 */
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
}
