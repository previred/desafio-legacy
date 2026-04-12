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

/**
 * Servlet REST para la gestion de empleados.
 *
 * <p>Este servlet expone los siguientes endpoints:</p>
 * <ul>
 *   <li>{@code GET /api/empleados} - Lista todos los empleados</li>
 *   <li>{@code POST /api/empleados} - Crea un nuevo empleado</li>
 *   <li>{@code DELETE /api/empleados/{id}} - Elimina un empleado por ID</li>
 * </ul>
 *
 * <p>Todas las respuestas son en formato JSON con el charset UTF-8.
 * Los errores son manejados centralmente via {@link ApiExceptionResolver}.</p>
 *
 * @see EmpleadoService
 * @see ApiExceptionResolver
 * @since 1.0
 */
@Component
@WebServlet("/api/empleados/*")
public class EmpleadoServlet extends HttpServlet {

    /** Logger para trazabilidad de peticiones HTTP. */
    private static final Logger LOG = LoggerFactory.getLogger(EmpleadoServlet.class);

    /** Codificacion de caracteres utilizada. */
    private static final String UTF_8 = "UTF-8";

    /** Content-Type para respuestas JSON. */
    private static final String APPLICATION_JSON = "application/json";

    /** Servicio de logica de negocio de empleados. */
    private final EmpleadoService empleadoService;

    /** ObjectMapper para serializacion/deserializacion JSON. */
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    /** Resolvedor de excepciones para respuestas de error. */
    private final ApiExceptionResolver apiExceptionResolver;

    /**
     * Constructor con dependencias inyectadas.
     *
     * @param empleadoService     servicio de empleados
     * @param objectMapper        serializador JSON
     * @param apiExceptionResolver resolvedor de excepciones
     */
    public EmpleadoServlet(
            EmpleadoService empleadoService,
            com.fasterxml.jackson.databind.ObjectMapper objectMapper,
            ApiExceptionResolver apiExceptionResolver) {
        this.empleadoService = empleadoService;
        this.objectMapper = objectMapper;
        this.apiExceptionResolver = apiExceptionResolver;
    }

    /**
     * Establece la codificacion de caracteres de forma segura para request y response.
     *
     * @param req  peticion HTTP
     * @param resp respuesta HTTP
     */
    private void setCharacterEncodingSafe(HttpServletRequest req, HttpServletResponse resp) {
        try {
            req.setCharacterEncoding(UTF_8);
        } catch (UnsupportedEncodingException e) {
            LOG.warn("Codificacion UTF-8 no soportada para request", e);
        }
        resp.setCharacterEncoding(UTF_8);
    }

    /**
     * Escribe una respuesta JSON con el codigo de estado especificado.
     *
     * @param resp   respuesta HTTP
     * @param data   objeto a serializar como JSON
     * @param status codigo de estado HTTP
     */
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

    /**
     * Escribe una respuesta JSON con codigo de estado 200 OK.
     *
     * @param resp respuesta HTTP
     * @param data objeto a serializar como JSON
     */
    private void writeJsonResponse(HttpServletResponse resp, Object data) {
        writeJsonResponse(resp, data, HttpServletResponse.SC_OK);
    }

    /**
     * Ejecuta una accion dentro de una plantilla comun que prepara la respuesta JSON
     * y maneja excepciones de forma unificada.
     *
     * <p>Este metodo encapsula la plantilla operativa compartida por todos los
     * endpoints del servlet:</p>
     * <ul>
     *   <li>Preparacion del content-type y encoding UTF-8</li>
     *   <li>Ejecucion del caso de uso via el handler proporcionado</li>
     *   <li>Resolucion unificada de excepciones via ApiExceptionResolver</li>
     * </ul>
     *
     * @param req    peticion HTTP
     * @param resp   respuesta HTTP
     * @param handler accion a ejecutar con acceso al contexto HTTP
     * @throws IOException si ocurre un error de entrada/salida
     */
    private void executeJsonRequest(HttpServletRequest req,
                                     HttpServletResponse resp,
                                     JsonRequestHandler handler) throws IOException {
        prepareJsonResponse(req, resp);
        try {
            handler.handle();
        } catch (Exception ex) {
            writeResolvedError(resp, req, ex);
        }
    }

    /**
     * Interface funcional para handlers de solicitudes JSON.
     */
    @FunctionalInterface
    private interface JsonRequestHandler {
        void handle() throws Exception;
    }

    /**
     * Prepara la respuesta HTTP para contenido JSON.
     *
     * @param req  peticion HTTP
     * @param resp respuesta HTTP
     */
    private void prepareJsonResponse(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType(APPLICATION_JSON);
        setCharacterEncodingSafe(req, resp);
    }

    /**
     * Maneja solicitudes GET para listar todos los empleados.
     *
     * @param req  peticion HTTP
     * @param resp respuesta HTTP
     * @throws IOException si ocurre un error de entrada/salida
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LOG.debug("GET /api/empleados - Iniciando solicitud");

        executeJsonRequest(req, resp, () -> {
            List<Empleado> empleados = empleadoService.getAllEmpleados();
            LOG.debug("GET /api/empleados - Retornando {} empleados", empleados.size());
            writeJsonResponse(resp, empleados, HttpServletResponse.SC_OK);
        });
    }

    /**
     * Maneja solicitudes POST para crear un nuevo empleado.
     *
     * @param req  peticion HTTP conteniendo el EmpleadoRequest en JSON
     * @param resp respuesta HTTP con el empleado creado
     * @throws IOException si ocurre un error de entrada/salida
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LOG.debug("POST /api/empleados - Iniciando solicitud");

        executeJsonRequest(req, resp, () -> {
            EmpleadoRequest request = parseEmpleadoRequest(req);
            Empleado empleadoCreado = empleadoService.crearEmpleado(request);
            LOG.info("POST /api/empleados - Empleado creado exitosamente");
            writeJsonResponse(resp, empleadoCreado, HttpServletResponse.SC_CREATED);
        });
    }

    /**
     * Maneja solicitudes DELETE para eliminar un empleado.
     *
     * @param req  peticion HTTP con el ID del empleado en la ruta
     * @param resp respuesta HTTP (sin contenido en exito)
     * @throws IOException si ocurre un error de entrada/salida
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LOG.debug("DELETE /api/empleados - Iniciando solicitud");

        executeJsonRequest(req, resp, () -> {
            Long id = parseEmpleadoId(req);
            eliminarEmpleado(id);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        });
    }

    /**
     * Parsea el cuerpo de la peticion HTTP a un objeto EmpleadoRequest.
     *
     * @param req peticion HTTP
     * @return EmpleadoRequest parseado
     * @throws IOException si ocurre un error al leer el body o parsear JSON
     */
    private EmpleadoRequest parseEmpleadoRequest(HttpServletRequest req) throws IOException {
        return objectMapper.readValue(req.getInputStream(), EmpleadoRequest.class);
    }

    /**
     * Extrae y valida el ID del empleado desde la ruta de la peticion.
     *
     * @param req peticion HTTP con el ID en la ruta (ej: /api/empleados/123)
     * @return ID del empleado parseado
     * @throws ValidationException si el ID no esta presente o tiene formato invalido
     */
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

    /**
     * Elimina un empleado y lanza excepcion si no es encontrado.
     *
     * @param id identificador del empleado a eliminar
     * @throws ResourceNotFoundException si el empleado no existe
     */
    private void eliminarEmpleado(Long id) {
        boolean eliminado = empleadoService.eliminarEmpleado(id);
        if (!eliminado) {
            throw new ResourceNotFoundException("id", "Empleado no encontrado con id: " + id);
        }
    }

    /**
     * Resuelve una excepcion y escribe la respuesta de error apropiada.
     *
     * @param resp respuesta HTTP
     * @param req  peticion HTTP
     * @param ex   excepcion a resolver
     */
    private void writeResolvedError(HttpServletResponse resp, HttpServletRequest req, Exception ex) {
        ResolvedErrorResponse resolved = apiExceptionResolver.resolve(ex, req.getRequestURI());
        writeJsonResponse(resp, resolved.getBody(), resolved.getStatus());
    }
}