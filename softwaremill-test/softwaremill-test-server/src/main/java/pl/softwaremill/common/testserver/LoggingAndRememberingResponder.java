package pl.softwaremill.common.testserver;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoggingAndRememberingResponder implements Responder {

    private static final Logger log = LoggerFactory.getLogger(LoggingAndRememberingResponder.class);

    private List<RequestInfo> requestsReceived = new ArrayList<RequestInfo>();

    @Override
    public boolean canRespond(HttpServletRequest request) {
        return true;
    }

    @Override
    public void respond(HttpServletRequest request, HttpServletResponse response) throws IOException {
        RequestInfo info = new RequestInfo(request);

        requestsReceived.add(info);
        log.info(info.toString());

        sendResponse(response);
    }

    private void sendResponse(HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().print("OK");
        response.getWriter().close();
    }

    public List<RequestInfo> getRequestsReceived() {
        return requestsReceived;
    }

    public class RequestInfo {

        private final String method;

        private final String path;
        private final Map<String, String[]> parameters;
        private final String content;
        private final Map<String, String[]> headers;

        public RequestInfo(HttpServletRequest request) throws IOException {
            this.method = request.getMethod();
            this.path = request.getPathInfo();
            this.headers = retrieveHeaders(request);
            this.parameters = request.getParameterMap();
            this.content = IOUtils.toString(request.getInputStream());
        }

        public String getMethod() {
            return method;
        }

        public String getPath() {
            return path;
        }

        public Map<String, String[]> getParameters() {
            return parameters;
        }

        public String getContent() {
            return content;
        }

        public Map<String, String[]> getHeaders() {
            return headers;
        }

        @Override
        public String toString() {
            return method + " " + path + formatMap(headers) + formatMap(parameters) + formatContent(content);
        }

        private String formatContent(String content) {
            if (Strings.isNullOrEmpty(content)) {
                return "";
            }

            return "\n----Content:\n" + content;
        }

        private String formatMap(Map<String, String[]> parametersMap) {
            String params = "";
            for(String paramName : parametersMap.keySet()) {
                params += paramName + "=" + Joiner.on(", ").join(parametersMap.get(paramName)) + "\n";
            }

            if (!params.isEmpty()) {
                params = "\n" + params;
            }

            return params;
        }

        private Map<String, String[]> retrieveHeaders(HttpServletRequest request) {
            Map<String, String[]> headers = new HashMap<String, String[]>();

            for(String headerName : Collections.list(request.getHeaderNames())) {
                headers.put(headerName, Collections.list(request.getHeaders(headerName)).toArray(new String[0]));
            }

            return headers;
        }

    }
}
