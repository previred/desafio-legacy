package cl.previred.desafio.servlet;

import cl.previred.desafio.dto.EmpleadoRequest;
import cl.previred.desafio.exception.ApiExceptionResolver;
import cl.previred.desafio.exception.ResolvedErrorResponse;
import cl.previred.desafio.exception.ResourceNotFoundException;
import cl.previred.desafio.exception.ValidationException;
import cl.previred.desafio.model.Empleado;
import cl.previred.desafio.service.EmpleadoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;

@Component
@WebServlet("/api/empleados/*")
public class EmpleadoServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(EmpleadoServlet.class);

    private static final String UTF_8 = "UTF-8";

    private static final String APPLICATION_JSON = "application/json";

    private final EmpleadoService empleadoService;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;
    private final ApiExceptionResolver apiExceptionResolver;

    public EmpleadoServlet(
            EmpleadoService empleadoService,
            com.fasterxml.jackson.databind.ObjectMapper objectMapper,
            ApiExceptionResolver apiExceptionResolver) {
        this.empleadoService = empleadoService;
        this.objectMapper = objectMapper;
        this.apiExceptionResolver = apiExceptionResolver;
    }

    private void setCharacterEncodingSafe(HttpServletRequest req, HttpServletResponse resp) {
        try {
            req.setCharacterEncoding(UTF_8);
        } catch (UnsupportedEncodingException e) {
            LOG.warn("Codificacion UTF-8 no soportada para request", e);
        }
        resp.setCharacterEncoding(UTF_8);
    }

    private void writeJsonResponse(HttpServletResponse resp, Object data, int status) {
        try (PrintWriter writer = resp.getWriter()) {
            resp.setStatus(status);
            writer.write(objectMapper.writeValueAsString(data));
        } catch (JsonProcessingException e) {
            LOG.error("Error al serializar respuesta JSON", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (IOException e) {
            LOG.error("Error al escribir respuesta", e);
        }
    }

    private void writeJsonResponse(HttpServletResponse resp, Object data) {
        writeJsonResponse(resp, data, HttpServletResponse.SC_OK);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LOG.debug("GET /api/empleados - Iniciando solicitud");
        resp.setContentType(APPLICATION_JSON);
        setCharacterEncodingSafe(req, resp);

        List<Empleado> empleados = empleadoService.getAllEmpleados();
        LOG.debug("GET /api/empleados - Retornando {} empleados", empleados.size());
        writeJsonResponse(resp, empleados);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LOG.debug("POST /api/empleados - Iniciando solicitud");
        resp.setContentType(APPLICATION_JSON);
        setCharacterEncodingSafe(req, resp);

        try {
            EmpleadoRequest request = parseEmpleadoRequest(req);
            Empleado empleadoCreado = empleadoService.crearEmpleado(request);
            LOG.info("POST /api/empleados - Empleado creado exitosamente");
            writeJsonResponse(resp, empleadoCreado, HttpServletResponse.SC_CREATED);
        } catch (Exception ex) {
            writeResolvedError(resp, req, ex);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LOG.debug("DELETE /api/empleados - Iniciando solicitud");
        resp.setContentType(APPLICATION_JSON);
        setCharacterEncodingSafe(req, resp);

        try {
            Long id = parseEmpleadoId(req);
            eliminarEmpleado(id);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (Exception ex) {
            writeResolvedError(resp, req, ex);
        }
    }

    private EmpleadoRequest parseEmpleadoRequest(HttpServletRequest req) throws IOException {
        return objectMapper.readValue(req.getInputStream(), EmpleadoRequest.class);
    }

    private Long parseEmpleadoId(HttpServletRequest req) {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            throw new ValidationException("id", "ID es requerido");
        }

        String idParam = pathInfo.substring(1);
        LOG.debug("DELETE /api/empleados/{} - Iniciando solicitud", idParam);

        try {
            return Long.parseLong(idParam);
        } catch (NumberFormatException ex) {
            throw new ValidationException("id", "ID invalido: " + idParam);
        }
    }

    private void eliminarEmpleado(Long id) {
        boolean eliminado = empleadoService.eliminarEmpleado(id);
        if (!eliminado) {
            throw new ResourceNotFoundException("id", "Empleado no encontrado con id: " + id);
        }
    }

    private void writeResolvedError(HttpServletResponse resp, HttpServletRequest req, Exception ex) {
        ResolvedErrorResponse resolved = apiExceptionResolver.resolve(ex, req.getRequestURI());
        writeJsonResponse(resp, resolved.getBody(), resolved.getStatus());
    }
}