package pl.softwaremill.common.paypal.process;

public interface PayPalProcessor {

    boolean accept(PayPalStatus status);

    void process(PayPalStatus status, PayPalParameters parameters);

    boolean isError();

    String getErrorMessage();

}
