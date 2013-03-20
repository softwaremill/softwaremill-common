package pl.softwaremill.common.paypal.process;

import java.util.logging.Logger;

/**
 * @Author: lukasz.zuchowski at gmail dot com
 * Date: 05.04.12
 * Time: 12:38
 */
public class TestErrorHandler extends PayPalErrorHandler {

    private Logger logger = Logger.getLogger(TestErrorHandler.class.getName());

    @Override
    public PayPalErrorHandler.ErrorMessage prepareErrorMessage() {
        return new ErrorMessage();
    }

    @Override
    public void processErrorMessage(PayPalErrorHandler.ErrorMessage errorMessage) {
        logger.info(errorMessage.toString());
    }

}
