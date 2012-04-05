package pl.softwaremill.common.paypal.service;

import pl.softwaremill.common.paypal.process.*;
import pl.softwaremill.common.paypal.process.processors.PayPalProcessor;
import pl.softwaremill.common.paypal.process.processors.PayPalProcessorsFactory;
import pl.softwaremill.common.paypal.process.status.DefaultPayPalStatusVerifier;
import pl.softwaremill.common.paypal.process.status.PayPalStatus;
import pl.softwaremill.common.paypal.process.status.PayPalStatusVerifier;

/**
 * @Author: lukasz.zuchowski at gmail dot com
 * Date: 05.04.12
 * Time: 12:14
 */
public class PayPalVerificationService {

    public static final PayPalStatusVerifier DefaultStatusVerifier = new DefaultPayPalStatusVerifier();

    private String payPalAddress;
    private PayPalErrorHandler errorHandler;
    private PayPalStatusVerifier statusVerifier;
    private PayPalProcessorsFactory processorsFactory;

    public PayPalVerificationService(String payPalAddress, PayPalProcessorsFactory palProcessorsFactory, PayPalErrorHandler errorHandler) {
        this(payPalAddress, palProcessorsFactory, errorHandler, DefaultStatusVerifier);
    }

    public PayPalVerificationService(String payPalAddress, PayPalProcessorsFactory processorsFactory, PayPalErrorHandler errorHandler, PayPalStatusVerifier statusVerifier) {
        this.payPalAddress = payPalAddress;
        this.processorsFactory = processorsFactory;
        this.errorHandler = errorHandler;
        this.statusVerifier = statusVerifier;
    }

    public PayPalStatus verify(RequestParameters requestParameters) {
        PayPalStatus status = statusVerifier.verify(payPalAddress, requestParameters);

        // assign values
        PayPalParameters parameters = PayPalParameters.create(requestParameters);

        PayPalErrorHandler.ErrorMessage errorMessage = errorHandler.prepareErrorMessage();
        errorMessage.appendPayPalParameters(parameters);
        process(errorMessage, status, parameters);
        return status;
    }

    protected void process(PayPalErrorHandler.ErrorMessage errorMessage, PayPalStatus status, PayPalParameters parameters) {
        for (PayPalProcessor processor : processorsFactory.buildProcessors()) {
            if (processor.accept(status)) {
                processor.process(status, parameters);
                if (processor.isError()) {
                    errorMessage.appendProcessingError(processor.getErrorMessage());
                    errorHandler.processErrorMessage(errorMessage);
                }
                return;
            }
        }
        throw new IllegalStateException("Unable to fin proper PayPalProcessor for status:"+status);
    }

}
