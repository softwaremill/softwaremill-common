package pl.softwaremill.common.paypal.service;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class RequestMock implements ServletRequest {

    private HashMap<String, String[]> parametersMap;

    public RequestMock(HashMap<String, String[]> parametersMap) {
        this.parametersMap = parametersMap;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        final Iterator<String> it = parametersMap.keySet().iterator();

        return new Enumeration<String>() {
            @Override
            public boolean hasMoreElements() {
                return it.hasNext();
            }

            @Override
            public String nextElement() {
                return it.next();
            }
        };
    }

    @Override
    public String getParameter(String name) {
        String[] parameterValues = getParameterValues(name);
        return (parameterValues == null ? null : parameterValues[0]);
    }

    @Override
    public String[] getParameterValues(String name) {
        return parametersMap.get(name);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return parametersMap;
    }

    @Override
    public Object getAttribute(String name) {
        throw new RuntimeException("Not mocked");
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        throw new RuntimeException("Not mocked");
    }

    @Override
    public String getCharacterEncoding() {
        throw new RuntimeException("Not mocked");
    }

    @Override
    public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
        throw new RuntimeException("Not mocked");
    }

    @Override
    public int getContentLength() {
        throw new RuntimeException("Not mocked");
    }

    @Override
    public String getContentType() {
        throw new RuntimeException("Not mocked");
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        throw new RuntimeException("Not mocked");
    }

    @Override
    public String getProtocol() {
        throw new RuntimeException("Not mocked");
    }

    @Override
    public String getScheme() {
        throw new RuntimeException("Not mocked");
    }

    @Override
    public String getServerName() {
        throw new RuntimeException("Not mocked");
    }

    @Override
    public int getServerPort() {
        throw new RuntimeException("Not mocked");
    }

    @Override
    public BufferedReader getReader() throws IOException {
        throw new RuntimeException("Not mocked");
    }

    @Override
    public String getRemoteAddr() {
        throw new RuntimeException("Not mocked");
    }

    @Override
    public String getRemoteHost() {
        throw new RuntimeException("Not mocked");
    }

    @Override
    public void setAttribute(String name, Object o) {
        throw new RuntimeException("Not mocked");
    }

    @Override
    public void removeAttribute(String name) {
        throw new RuntimeException("Not mocked");
    }

    @Override
    public Locale getLocale() {
        throw new RuntimeException("Not mocked");
    }

    @Override
    public Enumeration<Locale> getLocales() {
        throw new RuntimeException("Not mocked");
    }

    @Override
    public boolean isSecure() {
        throw new RuntimeException("Not mocked");
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        throw new RuntimeException("Not mocked");
    }

    @Override
    public String getRealPath(String path) {
        throw new RuntimeException("Not mocked");
    }

    @Override
    public int getRemotePort() {
        throw new RuntimeException("Not mocked");
    }

    @Override
    public String getLocalName() {
        throw new RuntimeException("Not mocked");
    }

    @Override
    public String getLocalAddr() {
        throw new RuntimeException("Not mocked");
    }

    @Override
    public int getLocalPort() {
        throw new RuntimeException("Not mocked");
    }

    @Override
    public ServletContext getServletContext() {
        throw new RuntimeException("Not mocked");
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        throw new RuntimeException("Not mocked");
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
        throw new RuntimeException("Not mocked");
    }

    @Override
    public boolean isAsyncStarted() {
        throw new RuntimeException("Not mocked");
    }

    @Override
    public boolean isAsyncSupported() {
        throw new RuntimeException("Not mocked");
    }

    @Override
    public AsyncContext getAsyncContext() {
        throw new RuntimeException("Not mocked");
    }

    @Override
    public DispatcherType getDispatcherType() {
        throw new RuntimeException("Not mocked");
    }
}
