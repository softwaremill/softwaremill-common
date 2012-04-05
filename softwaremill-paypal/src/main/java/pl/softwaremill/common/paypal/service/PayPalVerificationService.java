package pl.softwaremill.common.paypal.service;

import pl.softwaremill.common.paypal.process.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author: lukasz.zuchowski at gmail dot com
 * Date: 05.04.12
 * Time: 12:14
 */
public class PayPalVerificationService {

    public static final String ENCODING = "UTF-8";

    private String payPalAddress;
    private VerifiedPayPalProcessor verifiedPayPalProcessor;
    private PayPalErrorProcessor payPalErrorProcessor;

    public PayPalVerificationService(String payPalAddress, VerifiedPayPalProcessor verifiedPayPalProcessor, PayPalErrorProcessor payPalErrorProcessor) {
        this.payPalAddress = payPalAddress;
        this.verifiedPayPalProcessor = verifiedPayPalProcessor;
        this.payPalErrorProcessor = payPalErrorProcessor;
    }

    public boolean verify(RequestParameters requestParameters) throws IOException {
        PayPalStatus status = checkPayPalStatus(payPalAddress, requestParameters);
        // assign values
        PayPalParameters parameters = PayPalParameters.create(requestParameters);

        PayPalErrorProcessor.ErrorMessage errorMessage = payPalErrorProcessor.prepareErrorMessage();
        errorMessage.appendPayPalParameters(parameters);
        return process(errorMessage, status, parameters);
    }

    protected PayPalStatus checkPayPalStatus(String url, RequestParameters requestParameters) throws IOException {
        // checks PayPal status with paypal
        URLConnection uc = new URL(url).openConnection();
        uc.setDoOutput(true);
        uc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        PrintWriter pw = new PrintWriter(uc.getOutputStream());
        pw.println(buildRequestString(requestParameters).toString());
        pw.close();

        BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
        PayPalStatus status = new PayPalStatus(in.readLine());
        in.close();
        return status;
    }

    private static StringBuilder buildRequestString(RequestParameters request) {
        StringBuilder str = new StringBuilder("cmd=_notify-validate");
        str.append(request.buildRequestParametersForUrl(ENCODING));
        return str;
    }


    protected boolean process(PayPalErrorProcessor.ErrorMessage errorMessage, PayPalStatus status, PayPalParameters parameters) {
        for (PayPalProcessor processor : listProcessors()) {
            if (processor.accept(status)) {
                processor.process(status, parameters);
                if (processor.isError()) {
                    payPalErrorProcessor.processErrorMessage(errorMessage, processor);
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    private Set<PayPalProcessor> listProcessors() {
        Set<PayPalProcessor> processors = new HashSet<PayPalProcessor>();
        processors.add(verifiedPayPalProcessor);
        processors.add(new InvalidPayPalProcessor());
        processors.add(new UnknownPayPalProcessor());
        return processors;
    }

}
