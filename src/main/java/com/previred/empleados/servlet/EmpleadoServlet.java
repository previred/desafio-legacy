package com.previred.empleados.servlet;

import com.google.gson.Gson;
import com.previred.empleados.exception.BusinessValidationException;
import com.previred.empleados.exception.EmpleadoNotFoundException;
import com.previred.empleados.model.EmpleadoModel;
import com.previred.empleados.model.ErrorResponse;
import com.previred.empleados.service.IEmpleadoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@WebServlet(urlPatterns = "/api/empleados/*")
public class EmpleadoServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(EmpleadoServlet.class);
    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final String ENCODING_UTF8 = "UTF-8";

    private transient IEmpleadoService empleadoService;
    private transient Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        WebApplicationContext context = WebApplicationContextUtils
                .getRequiredWebApplicationContext(getServletContext());
        this.empleadoService = context.getBean(IEmpleadoService.class);
        this.gson = new Gson();
        log.info("EmpleadoServlet inicializado");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        log.debug("GET /api/empleados");
        List<EmpleadoModel> empleados = empleadoService.listarEmpleados();
        writeJsonResponse(resp, HttpServletResponse.SC_OK, empleados);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        log.debug("POST /api/empleados");
        try {
            String body = readBody(req);
            EmpleadoModel model = gson.fromJson(body, EmpleadoModel.class);
            if (model == null) {
                writeErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                        "El cuerpo de la solicitud no puede estar vacío");
                return;
            }
            EmpleadoModel created = empleadoService.crearEmpleado(model);
            writeJsonResponse(resp, HttpServletResponse.SC_CREATED, created);
        } catch (BusinessValidationException e) {
            writeJsonResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                    new ErrorResponse("Error de validación", e.getErrores()));
        } catch (Exception e) {
            log.error("Error al crear empleado", e);
            writeErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error interno del servidor");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        log.debug("DELETE /api/empleados");
        try {
            Long id = extractIdFromPath(req);
            if (id == null) {
                writeErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                        "Se requiere el ID del empleado en la URL (ej: /api/empleados/1)");
                return;
            }
            empleadoService.eliminarEmpleado(id);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (EmpleadoNotFoundException e) {
            writeErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (NumberFormatException e) {
            writeErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "El ID debe ser un número válido");
        } catch (Exception e) {
            log.error("Error al eliminar empleado", e);
            writeErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error interno del servidor");
        }
    }

    private Long extractIdFromPath(HttpServletRequest req) {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            return null;
        }
        return Long.parseLong(pathInfo.substring(1));
    }

    private String readBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    private void writeJsonResponse(HttpServletResponse resp, int status, Object data) throws IOException {
        resp.setContentType(CONTENT_TYPE_JSON);
        resp.setCharacterEncoding(ENCODING_UTF8);
        resp.setStatus(status);
        resp.getWriter().write(gson.toJson(data));
    }

    private void writeErrorResponse(HttpServletResponse resp, int status, String message) throws IOException {
        ErrorResponse error = new ErrorResponse(message, Collections.emptyList());
        writeJsonResponse(resp, status, error);
    }
}
