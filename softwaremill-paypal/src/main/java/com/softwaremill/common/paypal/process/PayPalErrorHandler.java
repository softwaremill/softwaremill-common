package com.softwaremill.common.paypal.process;

import com.softwaremill.common.paypal.process.processors.PayPalProcessor;

import java.util.Date;

/**
 * @Author: lukasz.zuchowski at gmail dot com
 * Date: 05.04.12
 * Time: 12:18
 */
public abstract class PayPalErrorHandler {

    public static class ErrorMessage {

        private StringBuilder message;

        public ErrorMessage() {
            message = new StringBuilder("There was something wrong with processing Paypal payment.")
                    .append(" Please investigate. Payment data below.\n\n")
                    .append("Timestamp: ").append(new Date()).append("\n\n");
        }

        public void appendPayPalParameters(PayPalParameters parameters) {
            message.append("Parameters:\n\n").append(parameters).append("\n\n");
        }
        
        public void appendProcessingError(String processingError){
            message.append("Processing error:\n\n").append(processingError);
        }

        @Override
        public String toString() {
            return message.toString();
        }
    }

    public ErrorMessage prepareErrorMessage() {
        return new ErrorMessage();
    }

    public abstract void processErrorMessage(ErrorMessage errorMessage);
}
