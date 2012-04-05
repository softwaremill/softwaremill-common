package pl.softwaremill.common.paypal.process;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Enumeration;

/**
 * Read message from PayPal and add cmd with validate
 */
public class PayPalStatus {

    private String status;

    public static PayPalStatus verify(String url, HttpServletRequest request) throws IOException {
        // verify with paypal
        URLConnection uc = new URL(url).openConnection();
        uc.setDoOutput(true);
        uc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        PrintWriter pw = new PrintWriter(uc.getOutputStream());
        pw.println(buildRequestString(request).toString());
        pw.close();

        BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
        PayPalStatus status = new PayPalStatus(in.readLine());
        in.close();
        return status;
    }

    private static StringBuilder buildRequestString(HttpServletRequest request) throws UnsupportedEncodingException {
        Enumeration en = request.getParameterNames();
        StringBuilder str = new StringBuilder("cmd=_notify-validate");
        while (en.hasMoreElements()) {
            String paramName = (String) en.nextElement();
            String paramValue = request.getParameter(paramName);
            str.append("&").append(paramName).append("=").append(URLEncoder.encode(paramValue, "UTF-8"));
        }
        return str;
    }

    private PayPalStatus(String status) {
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
