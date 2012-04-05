package pl.softwaremill.common.paypal.process;

public class UnknownPayPalProcessor extends AbstractPayPalProcessor {

    @Override
    public boolean accept(PayPalStatus status) {
        return status.isUnknown();
    }

    @Override
    public void process(PayPalStatus status, PayPalParameters parameters) {
        setErrorHappened().appendError("Paypal replied some unknown response: ")
                .appendError(status.toString()).appendError(" for a request.\n\n");
    }
}
