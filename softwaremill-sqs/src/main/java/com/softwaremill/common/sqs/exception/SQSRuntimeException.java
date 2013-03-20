package com.softwaremill.common.sqs.exception;

/**
 * Runtime Exception wrapping SQS exceptions
 *
 * @author Jaroslaw Kijanowski - jarek@softwaremill.pl
 *         Date: Aug 17, 2010
 */
public class SQSRuntimeException extends RuntimeException{

    public SQSRuntimeException() {
        super();
    }

    public SQSRuntimeException(String message) {
        super(message);
    }

    public SQSRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public SQSRuntimeException(Throwable cause) {
        super(cause);
    }

}
