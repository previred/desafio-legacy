package cl.previred.desafio.servlet;

import cl.previred.desafio.dto.EmpleadoRequest;
import cl.previred.desafio.dto.ErrorResponseFactory;
import cl.previred.desafio.exception.ValidationExceptionList;
import cl.previred.desafio.model.Empleado;
import cl.previred.desafio.service.EmpleadoService;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
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

/**
 * Servlet REST para la gestion de empleados.
 *
 * <p>Expone endpoints de consulta, creacion y eliminacion sobre la ruta base
 * {@code /api/empleados}:</p>
 * <ul>
 *   <li>{@code GET /api/empleados} - Lista todos los empleados</li>
 *   <li>{@code POST /api/empleados} - Crea un nuevo empleado</li>
 *   <li>{@code DELETE /api/empleados/{id}} - Elimina un empleado por ID</li>
 * </ul>
 *
 * <p>Este servlet utiliza:</p>
 * <ul>
 *   <li>Jackson para serializacion/deserializacion JSON</li>
 *   <li>SLF4J para logging estructurado</li>
 *   <li>Inyeccion de dependencias via Spring</li>
 * </ul>
 *
 * <p>Las respuestas JSON se serializan con Jackson y los errores se construyen
 * de forma consistente mediante {@link ErrorResponseFactory}. Dependiendo del
 * caso, el servlet retorna HTTP 400 para errores de validacion o formato,
 * HTTP 404 cuando el empleado no existe y HTTP 500 para errores internos.</p>
 *
 * @see EmpleadoService
 * @see cl.previred.desafio.dto.ErrorResponse
 * @since 1.0
 */
@Component
@WebServlet("/api/empleados/*")
public class EmpleadoServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(EmpleadoServlet.class);

    private static final String UTF_8 = "UTF-8";

    private static final String APPLICATION_JSON = "application/json";

    private final EmpleadoService empleadoService;

    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    public EmpleadoServlet(EmpleadoService empleadoService, com.fasterxml.jackson.databind.ObjectMapper objectMapper) {
        this.empleadoService = empleadoService;
        this.objectMapper = objectMapper;
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
        } catch (ValidationExceptionList ex) {
            LOG.warn("POST /api/empleados - Validation errors: {}", ex.getErrores());
            writeJsonResponse(resp, ErrorResponseFactory.validationError(ex.getErrores()), HttpServletResponse.SC_BAD_REQUEST);
        } catch (InvalidFormatException ex) {
            LOG.warn("POST /api/empleados - Formato invalido: {}", ex.getMessage());
            String campo = ex.getPath().isEmpty() ? "json" : ex.getPath().get(0).getFieldName();
            writeJsonResponse(resp, ErrorResponseFactory.validationError(campo, "Formato invalido para el campo: " + campo), HttpServletResponse.SC_BAD_REQUEST);
        } catch (MismatchedInputException ex) {
            LOG.warn("POST /api/empleados - JSON invalido: {}", ex.getMessage());
            writeJsonResponse(resp, ErrorResponseFactory.jsonInvalidError(), HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception ex) {
            LOG.error("POST /api/empleados - Error al procesar solicitud", ex);
            writeJsonResponse(resp, ErrorResponseFactory.internalError("Error interno del servidor"), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LOG.debug("DELETE /api/empleados - Iniciando solicitud");
        resp.setContentType(APPLICATION_JSON);
        setCharacterEncodingSafe(req, resp);

        Long id = parseEmpleadoId(req, resp);
        if (id == null) {
            return;
        }

        eliminarEmpleado(resp, id);
    }

    private EmpleadoRequest parseEmpleadoRequest(HttpServletRequest req) throws IOException {
        return objectMapper.readValue(req.getInputStream(), EmpleadoRequest.class);
    }

    private Long parseEmpleadoId(HttpServletRequest req, HttpServletResponse resp) {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            LOG.warn("DELETE /api/empleados - ID no proporcionado");
            writeJsonResponse(resp, ErrorResponseFactory.idRequiredError(), HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }

        String idParam = pathInfo.substring(1);
        LOG.debug("DELETE /api/empleados/{} - Iniciando solicitud", idParam);

        try {
            return Long.parseLong(idParam);
        } catch (NumberFormatException ex) {
            LOG.warn("DELETE /api/empleados - ID invalido: {}", idParam);
            writeJsonResponse(resp, ErrorResponseFactory.idInvalidError(idParam), HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
    }

    private void eliminarEmpleado(HttpServletResponse resp, Long id) {
        boolean eliminado = empleadoService.eliminarEmpleado(id);

        if (eliminado) {
            LOG.info("DELETE /api/empleados/{} - Empleado eliminado", id);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else {
            LOG.warn("DELETE /api/empleados/{} - Empleado no encontrado", id);
            writeJsonResponse(resp, ErrorResponseFactory.empleadoNotFoundError(id), HttpServletResponse.SC_NOT_FOUND);
        }
    }
}