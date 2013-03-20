package com.softwaremill.common.sqs.util;

/**
 * Wrapper class for message received from a SQS queue and converted to an object
 * and the SQS Message handle for further reference (to delete or modify (timeout) a message)
 *
 * @author Jaroslaw Kijanowski - jarek@softwaremill.pl
 *         Date: Aug 16, 2010
 */
public class SQSAnswer {

    private final Object message;
    private final String receiptHandle;

    public SQSAnswer(Object message, String receiptHandle) {
        this.message = message;
        this.receiptHandle = receiptHandle;
    }

    public Object getMessage() {
        return message;
    }

    public String getReceiptHandle() {
        return receiptHandle;
    }

}