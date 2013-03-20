package com.softwaremill.common.paypal.process.status;

/**
 * @Author: lukasz.zuchowski at gmail dot com
 * Date: 05.04.12
 * Time: 13:18
 */
public class VerificationException extends RuntimeException {

    public VerificationException(Throwable cause) {
        super(cause);
    }
}
