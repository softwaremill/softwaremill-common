package pl.softwaremill.common.paypal.service;

import org.testng.annotations.Test;
import pl.softwaremill.common.paypal.process.RequestParameters;
import pl.softwaremill.common.paypal.process.TestErrorHandler;
import pl.softwaremill.common.paypal.process.processors.MockVerifiedPayPalProcessor;
import pl.softwaremill.common.paypal.process.processors.PayPalProcessorsFactory;
import pl.softwaremill.common.paypal.process.status.MockStatusVerifier;
import pl.softwaremill.common.paypal.process.status.PayPalStatus;
import pl.softwaremill.common.paypal.process.status.PayPalStatusVerifier;

import java.util.HashMap;
import java.util.logging.Logger;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @Author: lukasz.zuchowski at gmail dot com
 * Date: 05.04.12
 * Time: 14:23
 */
public class PayPalVerificationServiceTest {

    private Logger log = Logger.getLogger(PayPalVerificationServiceTest.class.getName());


    @Test
    public void shouldVerifyWithSuccess() {
        log.info("--shouldVerifyWithSuccess");
        //given
        PayPalStatusVerifier mockStatusVerifier = new MockStatusVerifier();
        PayPalVerificationService verificationService = new PayPalVerificationService("dummyAddress",
                new PayPalProcessorsFactory(MockVerifiedPayPalProcessor.class),
                new TestErrorHandler(),
                mockStatusVerifier);
        HashMap<String, String[]> parametersMap = new HashMap<String, String[]>();
        parametersMap.put(RequestParameters.Parameter.payment_status.toString(), new String[]{"VERIFIED"});

        //when
        PayPalStatus status = verificationService.verify(new RequestParameters(new RequestMock(parametersMap)));

        //then
        assertThat(status.isVerified()).isTrue();
    }

    @Test
    public void shouldVerifyWithFailure() {
        log.info("--shouldVerifyWithFailure");
        //given
        PayPalStatusVerifier mockStatusVerifier = new MockStatusVerifier();
        PayPalVerificationService verificationService = new PayPalVerificationService("dummyAddress",
                new PayPalProcessorsFactory(MockVerifiedPayPalProcessor.class),
                new TestErrorHandler(),
                mockStatusVerifier);
        HashMap<String, String[]> parametersMap = new HashMap<String, String[]>();
        parametersMap.put(RequestParameters.Parameter.payment_status.toString(), new String[]{"INVALID"});
        //when
        PayPalStatus status = verificationService.verify(new RequestParameters(new RequestMock(parametersMap)));

        //then
        assertThat(status.isInvalid()).isTrue();
    }

    @Test
    public void shouldVerifyWithUnknown() {
        log.info("--shouldVerifyWithUnknown");
        //given
        PayPalStatusVerifier mockStatusVerifier = new MockStatusVerifier();
        PayPalVerificationService verificationService = new PayPalVerificationService("dummyAddress",
                new PayPalProcessorsFactory(MockVerifiedPayPalProcessor.class),
                new TestErrorHandler(),
                mockStatusVerifier);
        HashMap<String, String[]> parametersMap = new HashMap<String, String[]>();
        //when
        parametersMap.put(RequestParameters.Parameter.payment_status.toString(), new String[]{"UNKNOWN"});
        PayPalStatus status = verificationService.verify(new RequestParameters(new RequestMock(parametersMap)));

        //then
        assertThat(status.isUnknown()).isTrue();
    }


}
