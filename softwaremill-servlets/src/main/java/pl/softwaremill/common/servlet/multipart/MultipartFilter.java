/*
* net/balusc/http/multipart/MultipartFilter.java
*
* Copyright (C) 2009 BalusC
*
* Changes by Pawel Stawicki
*
* This program is free software: you can redistribute it and/or modify it under the terms of the
* GNU Lesser General Public License as published by the Free Software Foundation, either version 3
* of the License, or (at your option) any later version.
*
* This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License along with this library.
* If not, see <http://www.gnu.org/licenses/>.
*/

package pl.softwaremill.common.servlet.multipart;

import com.google.common.base.Strings;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
* This filter detects <tt>multipart/form-data</tt> and <tt>multipart/mixed</tt> POST requests and
* will then replace the <code>HttpServletRequest</code> by a <code>{@link HttpMultipartRequest}</code>.
*
* @author BalusC, Pawel Stawicki
* @link http://balusc.blogspot.com/2009/12/uploading-files-in-servlet-30.html
*/

public class MultipartFilter implements Filter {

    // Constants ----------------------------------------------------------------------------------

    //Init params names
    private static final String INIT_PARAM_MAX_CONTENT_LENGTH = "maxContentLength";
    private static final String INIT_PARAM_MAX_CONTENT_IN_MEM = "maxContentToKeepInMemory";
    private static final String INIT_PARAM_ON_MAX_LENGTH = "onMaxLength";
    private static final String INIT_PARAM_ENCODING = "defaultEncoding";
    private static final String INIT_PARAM_PROGRESS_LISTENER = "progressListener";

    private static final String REQUEST_METHOD_POST = "POST";
    private static final String CONTENT_TYPE_MULTIPART = "multipart/";
    private static final String ABORT = "abort";
    private static final String IGNORE = "ignore";

    // Vars --------------------------------------------------------------------------------------
    private String location;
    private long maxContentLength;
    private long maxContentToKeepInMem;
    private int onMaxLength;
    private String encoding;
    private ProgressListener progressListener = null;

    // Actions ------------------------------------------------------------------------------------

    @Override
    public void init(FilterConfig config) throws ServletException {
        this.maxContentLength = parseParameterValue(config, INIT_PARAM_MAX_CONTENT_LENGTH, 2048000);
        this.maxContentToKeepInMem = parseParameterValue(config, INIT_PARAM_MAX_CONTENT_IN_MEM, 1024);
        

        String onMaxLengthParam = getParameterValue(config, INIT_PARAM_ON_MAX_LENGTH, ABORT);
        if (ABORT.equalsIgnoreCase(onMaxLengthParam)) {
            onMaxLength = HttpMultipartRequest.ABORT_ON_MAX_LENGTH;
        } else if (IGNORE.equalsIgnoreCase(onMaxLengthParam)) {
            onMaxLength = HttpMultipartRequest.IGNORE_ON_MAX_LENGTH;
        } else {
            throw new ServletException("Only " + ABORT + " or " + IGNORE + " values are allowed" +
                    "for " + INIT_PARAM_ON_MAX_LENGTH + " parameter for MultipartFilter");
        }

        encoding = getParameterValue(config, INIT_PARAM_ENCODING, "UTF-8");
        String progressListenerClassName = config.getInitParameter(INIT_PARAM_PROGRESS_LISTENER);
        if (!Strings.isNullOrEmpty(progressListenerClassName)) {
            try {
                Class<? extends ProgressListener> progressListenerClass =
                        (Class<? extends ProgressListener>) Class.forName(progressListenerClassName);
                progressListener = progressListenerClass.newInstance();
            } catch (Exception e) {
                throw new ServletException("Cannot create instance of ProgressListener. Class: "
                        + progressListenerClassName);
            }
        }
    }

    private String getParameterValue(FilterConfig config, String paramName, String defaultValue) {
        String paramValue = config.getInitParameter(paramName);
        if (Strings.isNullOrEmpty(paramValue)) {
            return defaultValue;
        } else {
            return paramValue;
        }
    }

    private long parseParameterValue(FilterConfig config, String paramName, long defaultVal)
            throws ServletException {
        String value = config.getInitParameter(paramName);
        if (Strings.isNullOrEmpty(value)) {
            return defaultVal;
        }

        try {
            return Long.parseLong(value);
        } catch(NumberFormatException e) {
            throw new ServletException("Invalid value for parameter " +
                    paramName +": " + value);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        if (isMultipartRequest(httpRequest)) {
            request = new HttpMultipartRequest(
                    httpRequest,
                    maxContentLength,
                    maxContentToKeepInMem,
                    onMaxLength,
                    encoding,
                    progressListener);
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // NOOP.
    }

    // Helpers ------------------------------------------------------------------------------------

    /**
     * Returns true if the given request is a multipart request.
     * @param request The request to be checked.
     * @return True if the given request is a multipart request.
     */
    public static final boolean isMultipartRequest(HttpServletRequest request) {
        String method = request.getMethod();
        String contentType = request.getContentType();
        return REQUEST_METHOD_POST.equalsIgnoreCase(method)
            && contentType != null
            && contentType.toLowerCase().startsWith(CONTENT_TYPE_MULTIPART);
    }

}