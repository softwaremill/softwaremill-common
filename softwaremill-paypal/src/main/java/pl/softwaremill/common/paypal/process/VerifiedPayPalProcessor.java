package pl.softwaremill.common.paypal.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class VerifiedPayPalProcessor extends AbstractPayPalProcessor {

    protected final static Logger LOG = LoggerFactory.getLogger(VerifiedPayPalProcessor.class);

    @Override
    public boolean accept(PayPalStatus status) {
        return status.isVerified();
    }
}
