package com.softwaremill.common.testserver;

import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.security.Credential;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static org.eclipse.jetty.util.security.Constraint.__BASIC_AUTH;

/**
 * @author Maciej Bilas
 * @author Paweł Stawicki
 * @since 7/11/12 0:21
 */
public class TestServer {

    private final static int JETTY_PORT = 18182;
    private final static int JETTY_HTTPS_PORT = 18183;

    private final int port;
    private final int httpsPort;

    private Server server;
    private ConstraintSecurityHandler securityHandler = null;
    private List<Responder> responders = new ArrayList<Responder>();

    public TestServer() {
        this(JETTY_PORT, JETTY_HTTPS_PORT);
    }

    public TestServer(int port) {
        this(port, JETTY_HTTPS_PORT);
    }

    public TestServer(int port, int httpsPort) {
        this.port = port;
        this.httpsPort = httpsPort;
    }

    public void addResponder(Responder responder) {
        responders.add(responder);
    }

    public void start() throws Exception {
        server = new Server(port);

        buildSecurityHandlerIfNecessary();

        securityHandler.setHandler(createRespondersHandler());
        server.setHandler(securityHandler);

        ServerConnector sslConnector = createSslConnector();
        server.addConnector(sslConnector);

        server.start();
    }

    private ServerConnector createSslConnector() {
        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStorePath(getClass().getResource("/dummy.keystore").toExternalForm());
        sslContextFactory.setKeyStorePassword("qwerty");
        sslContextFactory.setKeyManagerPassword("qwerty");

        SslConnectionFactory sslConnectionFactory = new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString());
        HttpConnectionFactory httpConnectionFactory = new HttpConnectionFactory();

        ServerConnector connector = new ServerConnector(server, sslConnectionFactory, httpConnectionFactory);
        connector.setPort(httpsPort);

        return connector;
    }

    private Handler createRespondersHandler() {
        return new AbstractHandler() {
            @Override
            public void handle(String s, Request request, HttpServletRequest httpRequest, HttpServletResponse response) throws IOException, ServletException {
                for(Responder responder : responders) {
                    if (responder.canRespond(httpRequest)) {
                        request.setHandled(true);
                        responder.respond(request, response);

                        return;
                    }
                }

                response.setContentType("text/plain;charset=utf-8");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

                PrintWriter responseWriter = response.getWriter();
                try {
                    responseWriter.print("No responder could handle this request");
                } finally {
                    responseWriter.close();
                }
            }
        };

    }

    public void secure(String path, String username, String password) {
        buildSecurityHandlerIfNecessary();

        addConstraint(path, username, password);
    }

    private void buildSecurityHandlerIfNecessary() {
        if (securityHandler == null) {
            HashLoginService l = new HashLoginService();
            l.setName("realm");

            ConstraintSecurityHandler csh = new ConstraintSecurityHandler();
            csh.setAuthenticator(new BasicAuthenticator());
            csh.setRealmName("realm");
            csh.setLoginService(l);

            securityHandler = csh;
        }
    }

    private void addConstraint(String path, String username, String password) {
    /* Bad, evil casting :/ */
        HashLoginService l = (HashLoginService) securityHandler.getLoginService();
        l.putUser(username, Credential.getCredential(password), new String[]{"user"});

        org.eclipse.jetty.util.security.Constraint constraint = new org.eclipse.jetty.util.security.Constraint();
        constraint.setName(__BASIC_AUTH);
        constraint.setRoles(new String[]{"user"});
        constraint.setAuthenticate(true);

        ConstraintMapping cm = new ConstraintMapping();
        cm.setConstraint(constraint);
        cm.setPathSpec(path);

        securityHandler.addConstraintMapping(cm);
    }

    public void stop() throws Exception {
        server.stop();
    }

    public void clearResponders() {
        responders.clear();
    }
}
