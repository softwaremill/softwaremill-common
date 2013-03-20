package com.softwaremill.common.paypal.process.processors;

import com.softwaremill.common.paypal.process.PayPalParameters;
import com.softwaremill.common.paypal.process.status.PayPalStatus;

public interface PayPalProcessor {

    boolean accept(PayPalStatus status);

    void process(PayPalStatus status, PayPalParameters parameters);

    boolean isError();

    String getErrorMessage();

}
