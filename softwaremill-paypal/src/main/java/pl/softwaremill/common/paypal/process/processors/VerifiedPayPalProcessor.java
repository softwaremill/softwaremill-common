package pl.softwaremill.common.paypal.process.processors;

import pl.softwaremill.common.paypal.process.status.PayPalStatus;

public abstract class VerifiedPayPalProcessor extends AbstractPayPalProcessor {

    @Override
    public boolean accept(PayPalStatus status) {
        return status.isVerified();
    }
}
