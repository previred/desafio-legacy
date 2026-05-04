package com.previred.desafiolegacy.infrastructure.adapter.in;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(value = "/h2-console/*")
public class H2ConsoleServlet extends HttpServlet {
    private final org.h2.server.web.WebServlet h2Servlet = new org.h2.server.web.WebServlet();

    @Override
    public void init() throws ServletException {
        ServletConfig config = getServletConfig();
        h2Servlet.init(config);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        h2Servlet.service(req, resp);
    }
}