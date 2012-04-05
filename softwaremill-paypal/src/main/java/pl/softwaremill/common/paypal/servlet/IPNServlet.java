package pl.softwaremill.common.paypal.servlet;

import pl.softwaremill.common.paypal.process.InvalidPayPalProcessor;
import pl.softwaremill.common.paypal.process.PayPalParameters;
import pl.softwaremill.common.paypal.process.PayPalProcessor;
import pl.softwaremill.common.paypal.process.PayPalStatus;
import pl.softwaremill.common.paypal.process.UnknownPayPalProcessor;
import pl.softwaremill.common.paypal.process.VerifiedPayPalProcessor;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Servlet for handling paypal requests
 * <p/>
 * User: szimano
 */
public abstract class IPNServlet extends HttpServlet {

    private static final String SANDBOX_PAYPAL_ADDRESS = "https://www.sandbox.paypal.com/cgi-bin/webscr";
    private static final String PAYPAL_ADDRESS = "https://www.paypal.com/cgi-bin/webscr";

    private String usedPaypalAddress;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        if (config.getInitParameter("paypal.sandbox").toLowerCase().equals("true")) {
            usedPaypalAddress = SANDBOX_PAYPAL_ADDRESS;
        }
        else {
            usedPaypalAddress = PAYPAL_ADDRESS;
        }
    }

    //******************************************************************************

    protected abstract void processErrorMessage(StringBuilder errorEmail, PayPalProcessor processor);

    protected abstract <T extends VerifiedPayPalProcessor> T getVerifiedProcessor();

    //******************************************************************************

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        StringBuilder errorEmail = prepareErrorMessage();
        PayPalStatus status = PayPalStatus.verify(usedPaypalAddress, request);
        // assign values
        PayPalParameters parameters = PayPalParameters.create(request);
        errorEmail.append("Parameters:\n\n").append(parameters).append("\n\n");
        process(errorEmail, status, parameters);
    }

    private void process(StringBuilder errorMessage, PayPalStatus status, PayPalParameters parameters) {
        for (PayPalProcessor processor : listProcessors()) {
            if (processor.accept(status)) {
                processor.process(status, parameters);
                if (processor.isError()) {
                    processErrorMessage(errorMessage, processor);
                }
                return;
            }
        }
    }

    private StringBuilder prepareErrorMessage() {
        return new StringBuilder("There was something wrong with processing Paypal payment.")
                .append(" Please investigate. Payment data below.\n\n")
                .append("Timestamp: ").append(new Date()).append("\n\n");
    }


    private Set<PayPalProcessor> listProcessors() {
        Set<PayPalProcessor> processors = new HashSet<PayPalProcessor>();
        processors.add(getVerifiedProcessor());
        processors.add(new InvalidPayPalProcessor());
        processors.add(new UnknownPayPalProcessor());
        return processors;
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendError(500, "GET not supported");
    }

}