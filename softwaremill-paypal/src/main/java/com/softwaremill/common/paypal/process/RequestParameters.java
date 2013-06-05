package com.softwaremill.common.paypal.process;

import javax.servlet.ServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: lukasz.zuchowski at gmail dot com
 * Date: 05.04.12
 * Time: 11:17
 */
public class RequestParameters {

    private ServletRequest request;

    private static final Pattern HACKY_PARAMS = Pattern.compile("([^%^0^D])%0A");

    public enum Parameter {
        item_name,
        item_number,
        payment_status,
        mc_gross,
        mc_currency,
        txn_id,
        receiver_email,
        payer_email,
        custom,
        option_selection1,
        parent_txn_id,
        invoice
    }

    public RequestParameters(ServletRequest request) {
        this.request = request;
    }

    public String getParameter(Parameter parameter) {
        return getParameter(parameter.toString());
    }

    private String getParameter(String parameterName) {
        return request.getParameter(parameterName);
    }

    public String buildRequestParametersForUrl() {
        try {
            StringBuffer postBackMessage = new StringBuffer();

            String charset = request.getParameter("charset");
            if (charset == null) {
                charset = "UTF-8";
            }

            Enumeration<String> paramNames = request.getParameterNames();
            while (paramNames.hasMoreElements()) {
                String paramName = paramNames.nextElement();

                postBackMessage.append("&");
                postBackMessage.append(paramName);
                postBackMessage.append("=");

                String parameter = URLEncoder.encode(request.getParameter(paramName), charset);

                postBackMessage.append(fixTheParameterForIPN(parameter));
            }

            return postBackMessage.toString();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private String fixTheParameterForIPN(String parameter) {
        // hack from http://www.hebtech.co.uk/blog/paypal-ipn-invalid-on-live-server-but-valid-on-test-server-fixed/

        Matcher matcher = HACKY_PARAMS.matcher(parameter);
        StringBuffer s = new StringBuffer();

        while (matcher.find()) {
            matcher.appendReplacement(s, matcher.group(1) + "%0D%0A");
        }

        return s.toString();
    }
}
