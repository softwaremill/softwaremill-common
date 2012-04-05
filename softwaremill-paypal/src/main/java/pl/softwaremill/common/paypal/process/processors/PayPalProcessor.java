package pl.softwaremill.common.paypal.process.processors;

import pl.softwaremill.common.paypal.process.PayPalParameters;
import pl.softwaremill.common.paypal.process.status.PayPalStatus;

public interface PayPalProcessor {

    boolean accept(PayPalStatus status);

    void process(PayPalStatus status, PayPalParameters parameters);

    boolean isError();

    String getErrorMessage();

}
