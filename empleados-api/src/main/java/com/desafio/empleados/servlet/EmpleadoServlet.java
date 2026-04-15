package com.desafio.empleados.servlet;

import com.desafio.empleados.api.ApiFields;
import com.desafio.empleados.dto.EmpleadoAltaRequest;
import com.desafio.empleados.dto.EmpleadoMapper;
import com.desafio.empleados.dto.EmpleadoResponse;
import com.desafio.empleados.exception.PersistenciaEmpleadosException;
import com.desafio.empleados.model.Empleado;
import com.desafio.empleados.model.ErrorRegistro;
import com.desafio.empleados.model.RespuestaErrorValidacion;
import com.desafio.empleados.service.EmpleadoService;
import com.desafio.empleados.service.EmpleadoService.ResultadoAlta;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EmpleadoServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(EmpleadoServlet.class);

    static final String ALLOW_METHODS = "GET, POST, DELETE, HEAD, OPTIONS";

    private final EmpleadoService empleadoService;
    private final ObjectMapper objectMapper;

    public EmpleadoServlet(EmpleadoService empleadoService, ObjectMapper objectMapper) {
        this.empleadoService = empleadoService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String m = req.getMethod();
        if ("PUT".equals(m) || "PATCH".equals(m)) {
            responderMetodoNoPermitido(resp);
            return;
        }
        super.service(req, resp);
    }

    private void responderMetodoNoPermitido(HttpServletResponse resp) throws IOException {
        prepararJson(resp);
        resp.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        resp.setHeader("Allow", ALLOW_METHODS);
        objectMapper.writeValue(resp.getOutputStream(), mensajeSimple("Método no permitido."));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        prepararJson(resp);
        String pathInfo = req.getPathInfo();
        if (tieneSubrecursoInvalidoParaListar(pathInfo)) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        try {
            List<EmpleadoResponse> lista = empleadoService.listar().stream()
                    .map(EmpleadoMapper::toResponse)
                    .collect(Collectors.toList());
            objectMapper.writeValue(resp.getOutputStream(), lista);
        } catch (PersistenciaEmpleadosException e) {
            responderFalloPersistencia(resp, "GET /api/empleados", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        prepararJson(resp);
        String pathInfo = req.getPathInfo();
        if (pathInfo != null && !pathInfo.isEmpty() && !"/".equals(pathInfo)) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        if (!"application/json".equalsIgnoreCase(parteTipoMedia(req.getContentType()))) {
            resp.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
            return;
        }
        final Empleado empleado;
        try {
            EmpleadoAltaRequest alta = objectMapper.readValue(req.getInputStream(), EmpleadoAltaRequest.class);
            empleado = EmpleadoMapper.toNuevaEntidad(alta);
        } catch (JsonProcessingException e) {
            log.debug("POST /api/empleados: cuerpo JSON ilegible", e);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getOutputStream(), errorCuerpoNoJson());
            return;
        } catch (IOException e) {
            log.error("POST /api/empleados: error al leer el cuerpo", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            escribirErrorServidor(resp);
            return;
        }
        try {
            ResultadoAlta resultado = empleadoService.validarYGuardar(empleado);
            if (resultado.tieneErrores()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(resp.getOutputStream(), resultado.getErrores());
                return;
            }
            Empleado persistido = resultado.getEmpleado();
            resp.setStatus(HttpServletResponse.SC_CREATED);
            String location = req.getRequestURL().toString();
            if (persistido.getId() != null) {
                resp.setHeader("Location", location + "/" + persistido.getId());
            }
            objectMapper.writeValue(resp.getOutputStream(), EmpleadoMapper.toResponse(persistido));
        } catch (PersistenciaEmpleadosException e) {
            responderFalloPersistencia(resp, "POST /api/empleados", e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        prepararJson(resp);
        String pathInfo = req.getPathInfo();
        Optional<Long> idOpt = parseId(pathInfo);
        if (!idOpt.isPresent()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getOutputStream(),
                    mensajeSimple("Se requiere DELETE sobre /api/empleados/{id}"));
            return;
        }
        try {
            boolean borrado = empleadoService.eliminar(idOpt.get());
            if (!borrado) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                objectMapper.writeValue(resp.getOutputStream(), mensajeSimple("Empleado no encontrado."));
                return;
            }
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (PersistenciaEmpleadosException e) {
            responderFalloPersistencia(resp, "DELETE /api/empleados", e);
        }
    }

    private void responderFalloPersistencia(HttpServletResponse resp, String operacion, PersistenciaEmpleadosException e)
            throws IOException {
        log.error(operacion, e);
        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        escribirErrorServidor(resp);
    }

    private static RespuestaErrorValidacion errorCuerpoNoJson() {
        RespuestaErrorValidacion r = new RespuestaErrorValidacion();
        r.setErrores(Collections.singletonList(new ErrorRegistro(ApiFields.CAMPO_CUERPO,
                "El cuerpo de la petición no es JSON válido o está vacío.")));
        return r;
    }

    private static boolean tieneSubrecursoInvalidoParaListar(String pathInfo) {
        if (pathInfo == null || pathInfo.isEmpty() || "/".equals(pathInfo)) {
            return false;
        }
        return true;
    }

    private static Optional<Long> parseId(String pathInfo) {
        if (pathInfo == null || pathInfo.length() < 2) {
            return Optional.empty();
        }
        String s = pathInfo.startsWith("/") ? pathInfo.substring(1) : pathInfo;
        if (s.isEmpty()) {
            return Optional.empty();
        }
        try {
            return Optional.of(Long.parseLong(s.trim()));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private static String parteTipoMedia(String contentType) {
        if (contentType == null) {
            return "";
        }
        int semi = contentType.indexOf(';');
        return semi >= 0 ? contentType.substring(0, semi).trim() : contentType.trim();
    }

    private void prepararJson(HttpServletResponse resp) {
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("application/json;charset=UTF-8");
    }

    private void escribirErrorServidor(HttpServletResponse resp) throws IOException {
        objectMapper.writeValue(resp.getOutputStream(), mensajeSimple("Error interno del servidor."));
    }

    private static java.util.Map<String, String> mensajeSimple(String mensaje) {
        return Collections.singletonMap(ApiFields.MENSAJE, mensaje);
    }
}
