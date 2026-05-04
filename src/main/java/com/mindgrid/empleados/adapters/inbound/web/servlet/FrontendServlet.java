package com.mindgrid.empleados.adapters.inbound.web.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

@WebServlet({"", "/", "/index.html", "/static/css/*", "/static/js/*"})
public class FrontendServlet extends HttpServlet {

    private static final Map<String, String> CONTENT_TYPES = new HashMap<>();

    static {
        CONTENT_TYPES.put("html", "text/html;charset=UTF-8");
        CONTENT_TYPES.put("css",  "text/css");
        CONTENT_TYPES.put("js",   "application/javascript");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String uri = req.getRequestURI();

        if (uri.equals("/") || uri.isEmpty() || uri.equals("/index.html")) {
            uri = "/index.html";
        }

        String resourcePath = uri.startsWith("/static/") ? uri.substring(1) : "static" + uri;
        String ext = uri.substring(uri.lastIndexOf('.') + 1).toLowerCase();
        String contentType = CONTENT_TYPES.getOrDefault(ext, "application/octet-stream");

        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType(contentType);
            try (OutputStream os = resp.getOutputStream()) {
                byte[] buffer = new byte[4096];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
            }
        }
    }
}
