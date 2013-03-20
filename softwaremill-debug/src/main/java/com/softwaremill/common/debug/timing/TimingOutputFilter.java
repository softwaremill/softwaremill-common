package com.softwaremill.common.debug.timing;

import javax.servlet.*;
import java.io.IOException;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class TimingOutputFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException { }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } finally {
            TimingResults.instance.dumpCurrent();
        }
    }

    @Override
    public void destroy() { }
}
