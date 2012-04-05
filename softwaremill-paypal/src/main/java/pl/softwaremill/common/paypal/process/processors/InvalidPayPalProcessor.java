package pl.softwaremill.common.paypal.process.processors;

import pl.softwaremill.common.paypal.process.PayPalParameters;
import pl.softwaremill.common.paypal.process.PayPalStatus;

public class InvalidPayPalProcessor extends AbstractPayPalProcessor {

    @Override
    public boolean accept(PayPalStatus status) {
        return status.isInvalid();
    }

    @Override
    public void process(PayPalStatus status, PayPalParameters parameters) {
        setErrorHappened();
        appendError("Paypal replied INVALID for a request.\n\n");
    }
}
