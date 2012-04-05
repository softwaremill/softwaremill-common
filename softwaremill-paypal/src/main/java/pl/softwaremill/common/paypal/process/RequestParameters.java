package pl.softwaremill.common.paypal.process;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @Author: lukasz.zuchowski at gmail dot com
 * Date: 05.04.12
 * Time: 11:17
 */
public class RequestParameters {

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
        parent_txn_id

    }

    private final Map<String, String[]> parametersMap = new HashMap<String, String[]>();

    public RequestParameters(Map<String, String[]> parametersMap) {
        this.parametersMap.putAll(parametersMap);
    }

    public String getParameter(Parameter parameter) {
        return getParameter(parameter.toString());
    }

    private String getParameter(String parameterName) {
        String[] values = this.parametersMap.get(parameterName);
        return values.length > 0 ? values[0] : null;
    }

    public String buildRequestParametersForUrl(String encoding) {
        try {
            Iterator parametersIterator = parametersMap.keySet().iterator();
            StringBuilder str = new StringBuilder();
            while (parametersIterator.hasNext()) {
                String paramName = (String) parametersIterator.next();
                String paramValue = getParameter(paramName);
                str.append("&").append(paramName).append("=").append(URLEncoder.encode(paramValue, encoding));
            }
            return str.toString();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

}
