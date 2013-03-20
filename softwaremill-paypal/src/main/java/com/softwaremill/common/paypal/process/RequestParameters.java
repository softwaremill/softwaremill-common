package pl.softwaremill.common.paypal.process;

import javax.servlet.ServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Enumeration;

/**
 * @Author: lukasz.zuchowski at gmail dot com
 * Date: 05.04.12
 * Time: 11:17
 */
public class RequestParameters {

    private ServletRequest request;

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
                postBackMessage.append(URLEncoder.encode(request.getParameter(paramName), charset));
            }

            return postBackMessage.toString();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

}
