package pl.softwaremill.common.paypal.process.processors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.softwaremill.common.paypal.process.PayPalStatus;

public abstract class VerifiedPayPalProcessor extends AbstractPayPalProcessor {

    protected final static Logger LOG = LoggerFactory.getLogger(VerifiedPayPalProcessor.class);

    @Override
    public boolean accept(PayPalStatus status) {
        return status.isVerified();
    }
}
