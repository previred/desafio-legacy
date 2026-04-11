package cl.previred.desafio.servlet;

import cl.previred.desafio.dto.EmpleadoRequest;
import cl.previred.desafio.dto.ErrorResponse;
import cl.previred.desafio.dto.ValidationError;
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
import java.util.Collections;
import java.util.List;

/**
 * Servlet REST para la gestion de empleados.
 *
 * <p>Expone endpoints CRUD sobre la ruta base {@code /api/empleados}:</p>
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
 * <p>Los errores de validacion retornan HTTP 400 Bad Request con un
 * {@link ErrorResponse} conteniendo la lista de errores.</p>
 *
 * @see EmpleadoService
 * @see ErrorResponse
 * @since 1.0
 */
@Component
@WebServlet("/api/empleados/*")
public class EmpleadoServlet extends HttpServlet {

    /** Logger para trazabilidad de peticiones HTTP. */
    private static final Logger LOG = LoggerFactory.getLogger(EmpleadoServlet.class);

    /** Servicio de negocio para empleados. */
    private final EmpleadoService empleadoService;

    /** ObjectMapper para serializacion JSON. */
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    /**
     * Constructor con dependencias inyectadas por Spring.
     *
     * @param empleadoService servicio de empleados
     * @param objectMapper    mapper para JSON
     */
    public EmpleadoServlet(EmpleadoService empleadoService, com.fasterxml.jackson.databind.ObjectMapper objectMapper) {
        this.empleadoService = empleadoService;
        this.objectMapper = objectMapper;
    }

    /**
     * Maneja peticiones GET para listar todos los empleados.
     *
     * @param req  peticion HTTP
     * @param resp respuesta HTTP
     * @throws IOException si ocurre un error al escribir la respuesta
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LOG.debug("GET /api/empleados - Iniciando solicitud");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        List<Empleado> empleados = empleadoService.getAllEmpleados();
        LOG.debug("GET /api/empleados - Retornando {} empleados", empleados.size());
        resp.getWriter().write(objectMapper.writeValueAsString(empleados));
    }

    /**
     * Maneja peticiones POST para crear un nuevo empleado.
     *
     * <p>El cuerpo de la peticion debe ser JSON con los campos:</p>
     * <ul>
     *   <li>nombre (requerido)</li>
     *   <li>apellido (requerido)</li>
     *   <li>rut (requerido, unico)</li>
     *   <li>cargo (requerido)</li>
     *   <li>salario (requerido, >= $400,000)</li>
     *   <li>bono (opcional, <= 50% salario)</li>
     *   <li>descuentos (opcional, <= salario)</li>
     * </ul>
     *
     * <p>Respuestas posibles:</p>
     * <ul>
     *   <li>201 Created - Empleado creado exitosamente</li>
     *   <li>400 Bad Request - Error de validacion o JSON invalido</li>
     *   <li>500 Internal Server Error - Error interno</li>
     * </ul>
     *
     * @param req  peticion HTTP con cuerpo JSON
     * @param resp respuesta HTTP
     * @throws IOException si ocurre un error al procesar la peticion
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LOG.debug("POST /api/empleados - Iniciando solicitud");
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            EmpleadoRequest request = objectMapper.readValue(req.getInputStream(), EmpleadoRequest.class);
            LOG.debug("POST /api/empleados - Request parsed: RUT={}", request.getRut());
            empleadoService.crearEmpleado(request);

            List<Empleado> empleados = empleadoService.getAllEmpleados();
            Empleado ultimoCreado = empleados.isEmpty() ? null : empleados.get(empleados.size() - 1);
            LOG.info("POST /api/empleados - Empleado creado exitosamente");
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(objectMapper.writeValueAsString(ultimoCreado));

        } catch (ValidationExceptionList ex) {
            LOG.warn("POST /api/empleados - Validation errors: {}", ex.getErrores());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(objectMapper.writeValueAsString(new ErrorResponse(ex.getErrores())));
        } catch (InvalidFormatException ex) {
            LOG.warn("POST /api/empleados - Formato invalido: {}", ex.getMessage());
            String campo = ex.getPath().isEmpty() ? "json" : ex.getPath().get(0).getFieldName();
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(objectMapper.writeValueAsString(
                    new ErrorResponse(Collections.singletonList(new ValidationError(campo, "Formato invalido para el campo: " + campo)))));
        } catch (MismatchedInputException ex) {
            LOG.warn("POST /api/empleados - JSON invalido: {}", ex.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(objectMapper.writeValueAsString(
                    new ErrorResponse(Collections.singletonList(new ValidationError("json", "JSON invalido o no pudo ser parseado")))));
        } catch (Exception ex) {
            LOG.error("POST /api/empleados - Error al procesar solicitud", ex);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(objectMapper.writeValueAsString(
                    new ErrorResponse(Collections.singletonList(new ValidationError("internal", "Error interno del servidor")))));
        }
    }

    /**
     * Maneja peticiones DELETE para eliminar un empleado por ID.
     *
     * <p>El ID del empleado debe pasarse en la ruta URL:
     * {@code DELETE /api/empleados/{id}}</p>
     *
     * <p>Respuestas posibles:</p>
     * <ul>
     *   <li>204 No Content - Empleado eliminado exitosamente</li>
     *   <li>400 Bad Request - ID no proporcionado o invalido</li>
     *   <li>404 Not Found - Empleado no encontrado</li>
     * </ul>
     *
     * @param req  peticion HTTP con ID en la ruta
     * @param resp respuesta HTTP
     * @throws IOException si ocurre un error al escribir la respuesta
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            LOG.warn("DELETE /api/empleados - ID no proporcionado");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(objectMapper.writeValueAsString(
                    new ErrorResponse(Collections.singletonList(new ValidationError("id", "ID es requerido")))));
            return;
        }

        String idParam = pathInfo.substring(1);
        LOG.debug("DELETE /api/empleados/{} - Iniciando solicitud", idParam);

        try {
            Long id = Long.parseLong(idParam);
            boolean eliminado = empleadoService.eliminarEmpleado(id);

            if (eliminado) {
                LOG.info("DELETE /api/empleados/{} - Empleado eliminado", id);
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                LOG.warn("DELETE /api/empleados/{} - Empleado no encontrado", id);
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write(objectMapper.writeValueAsString(
                        new ErrorResponse(Collections.singletonList(new ValidationError("id", "Empleado no encontrado")))));
            }
        } catch (NumberFormatException ex) {
            LOG.warn("DELETE /api/empleados - ID invalido: {}", idParam);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(objectMapper.writeValueAsString(
                    new ErrorResponse(Collections.singletonList(new ValidationError("id", "ID invalido")))));
        }
    }
}
