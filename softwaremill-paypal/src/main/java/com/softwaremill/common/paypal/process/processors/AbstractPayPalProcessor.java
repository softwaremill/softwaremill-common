package com.softwaremill.common.paypal.process.processors;

public abstract class AbstractPayPalProcessor implements PayPalProcessor {

    private StringBuilder errorMessage = new StringBuilder();
    private boolean error = false;

    @Override
    public boolean isError() {
        return error;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage.toString();
    }

    protected AbstractPayPalProcessor appendError(String message) {
        errorMessage.append(message);
        return this;
    }

    protected AbstractPayPalProcessor setErrorHappened() {
        error = true;
        return this;
    }

}
