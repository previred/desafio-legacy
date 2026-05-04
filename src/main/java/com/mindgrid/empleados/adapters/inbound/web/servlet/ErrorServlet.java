package com.mindgrid.empleados.adapters.inbound.web.servlet;

import com.google.gson.Gson;
import com.mindgrid.empleados.adapters.inbound.web.response.ErrorResponse;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@WebServlet("/error")
public class ErrorServlet extends HttpServlet {

    private static final Gson GSON = new Gson();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer status = (Integer) req.getAttribute("javax.servlet.error.status_code");
        if (status == null) status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

        resp.setStatus(status);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(GSON.toJson(
            new ErrorResponse(Collections.singletonList(mensajePorStatus(status)))
        ));
    }

    private String mensajePorStatus(int status) {
        switch (status) {
            case 404: return "El recurso solicitado no existe";
            case 405: return "Método HTTP no permitido para este endpoint";
            default:  return "Error del servidor (HTTP " + status + ")";
        }
    }
}
