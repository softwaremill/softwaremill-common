package pl.softwaremill.common.paypal.process.status;

import com.google.common.base.Charsets;
import pl.softwaremill.common.paypal.process.RequestParameters;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

/**
 * Checks transaction status with PayPal
 * @Author: lukasz.zuchowski at gmail dot com
 * Date: 05.04.12
 * Time: 13:13
 */
public class DefaultPayPalStatusVerifier implements PayPalStatusVerifier {

    public PayPalStatus verify(String url, RequestParameters requestParameters) {
        try {
            // checks PayPal status with paypal
            URLConnection uc = new URL(url).openConnection();
            uc.setDoOutput(true);
            uc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(uc.getOutputStream(), Charsets.UTF_8));
            pw.println(buildRequestString(requestParameters).toString());
            pw.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream(), Charsets.UTF_8));
            PayPalStatus status = new PayPalStatus(in.readLine());
            in.close();
            return status;
        } catch (IOException e) {
            throw new VerificationException(e);
        }
    }


    private static StringBuilder buildRequestString(RequestParameters request) {
        StringBuilder str = new StringBuilder("cmd=_notify-validate");
        str.append(request.buildRequestParametersForUrl());
        return str;
    }
}
