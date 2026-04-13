package cl.maraneda.previred.filter;

import cl.maraneda.previred.wrapper.CachedBodyHttpServletRequest;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

public class CachedBodyFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpReq = (HttpServletRequest) request;

        CachedBodyHttpServletRequest wrapped =
                new CachedBodyHttpServletRequest(httpReq);

        chain.doFilter(wrapped, response);
    }
}
