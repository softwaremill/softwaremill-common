package pl.softwaremill.common.paypal.process.status;

import pl.softwaremill.common.paypal.process.RequestParameters;

import java.util.logging.Logger;

/**
 * @Author: lukasz.zuchowski at gmail dot com
 * Date: 05.04.12
 * Time: 15:13
 */
public class MockStatusVerifier implements PayPalStatusVerifier {

    private Logger logger = Logger.getLogger(MockStatusVerifier.class.getName());

    @Override
    public PayPalStatus verify(String url, RequestParameters requestParameters) {
        logger.info("with PayPal url:" + url);
        logger.info("verifying request parameter:" + requestParameters);
        //this is not how payPal works. Its just mock implementation.
        String status = requestParameters.getParameter(RequestParameters.Parameter.payment_status);
        return new PayPalStatus(status);
    }
}
