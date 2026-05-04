package com.mindgrid.empleados.adapters.inbound.web.exception;

import com.google.gson.Gson;
import com.mindgrid.empleados.adapters.inbound.web.response.ErrorResponse;
import com.mindgrid.empleados.domain.exception.BusinessException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class WebExceptionHandler {

    private static final Gson GSON = new Gson();

    private WebExceptionHandler() {}

    public static void handle(HttpServletResponse response, BusinessException ex) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(GSON.toJson(new ErrorResponse(ex.getErrores())));
    }

    public static void handleGeneric(HttpServletResponse response, String mensaje) throws IOException {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(GSON.toJson(new ErrorResponse(
            java.util.Collections.singletonList(mensaje)
        )));
    }
}
