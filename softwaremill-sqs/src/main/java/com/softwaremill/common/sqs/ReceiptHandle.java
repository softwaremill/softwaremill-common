package com.softwaremill.common.sqs;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Maciej Bilas
 * @since 12/10/12 12:27
 */
public class ReceiptHandle {

    private final String receiptHandle;

    public ReceiptHandle(String receiptHandle) {
        this.receiptHandle = checkNotNull(receiptHandle);
    }

    public String get() {
        return receiptHandle;
    }

    @Override
    public String toString() {
        return get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReceiptHandle)) return false;

        ReceiptHandle that = (ReceiptHandle) o;

        if (receiptHandle != null ? !receiptHandle.equals(that.receiptHandle) : that.receiptHandle != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return receiptHandle != null ? receiptHandle.hashCode() : 0;
    }
}
