package com.softwaremill.common.paypal.process.processors;

import com.softwaremill.common.paypal.process.PayPalParameters;
import com.softwaremill.common.paypal.process.status.PayPalStatus;
import com.softwaremill.common.paypal.service.PayPalVerificationServiceTest;

import java.util.logging.Logger;

/**
 * @Author: lukasz.zuchowski at gmail dot com
 * Date: 05.04.12
 * Time: 15:36
 */
public class MockVerifiedPayPalProcessor extends VerifiedPayPalProcessor {

    private Logger logger = Logger.getLogger(MockVerifiedPayPalProcessor.class.getName());

    @Override
    public void process(PayPalStatus status, PayPalParameters parameters) {
        logger.info("process with:" + status + "\n and PayPalParameters");
    }
}
