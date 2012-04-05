package pl.softwaremill.common.paypal.servlet;

import pl.softwaremill.common.paypal.process.*;
import pl.softwaremill.common.paypal.service.PayPalVerificationService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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

    protected abstract <T extends PayPalErrorProcessor> T getPayPalErrorProcessor();

    protected abstract <T extends VerifiedPayPalProcessor> T getVerifiedProcessor();

    //******************************************************************************

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PayPalVerificationService payPalVerificationService = new PayPalVerificationService(usedPaypalAddress,getVerifiedProcessor(),getPayPalErrorProcessor());
        payPalVerificationService.verify(new RequestParameters(request.getParameterMap()));
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendError(500, "GET not supported");
    }

}