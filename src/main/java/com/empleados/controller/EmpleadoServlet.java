package com.empleados.controller;

import com.empleados.api.generated.model.EmpleadoResponse;
import com.empleados.api.generated.model.ErrorResponse;
import com.empleados.api.generated.model.MensajeResponse;
import com.empleados.exception.business.RutDuplicadoException;
import com.empleados.model.Empleado;
import com.empleados.service.EmpleadoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Servlet HTTP que expone el endpoint {@code /api/empleados}.
 * <p>
 * Maneja las operaciones CRUD de empleados via HTTP:
 * <ul>
 *   <li><b>GET</b> — Lista todos los empleados</li>
 *   <li><b>POST</b> — Crea un nuevo empleado con validaciones</li>
 *   <li><b>DELETE /{id}</b> — Elimina un empleado por su ID</li>
 * </ul>
 * Los modelos de respuesta ({@link EmpleadoResponse}, {@link ErrorResponse},
 * {@link MensajeResponse}) son generados automaticamente desde la especificacion
 * OpenAPI mediante {@code openapi-generator-maven-plugin}.
 * </p>
 *
 * @see EmpleadoService
 */
@Slf4j
@RequiredArgsConstructor
public class EmpleadoServlet extends HttpServlet {

    private final EmpleadoService empleadoService;
    private final Validator validator;
    private final ObjectMapper objectMapper;

    /**
     * Lista todos los empleados en formato JSON.
     *
     * @param req  peticion HTTP
     * @param resp respuesta HTTP con status 200 y array JSON
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        log.info("GET /api/empleados");
        List<EmpleadoResponse> empleados = empleadoService.listarEmpleados();
        sendJson(resp, 200, empleados);
    }

    /**
     * Crea un nuevo empleado a partir del body JSON.
     * <p>
     * Ejecuta validaciones de Bean Validation ({@code @NotBlank}, {@code @Min},
     * {@code @Pattern}) y validaciones personalizadas ({@code @EmpleadoValido})
     * antes de persistir. Retorna 400 si alguna regla falla.
     * </p>
     *
     * @param req  peticion HTTP con body JSON del empleado
     * @param resp respuesta HTTP con status 201 (creado) o 400 (error de validacion)
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        log.info("POST /api/empleados");
        try {
            Empleado empleado = parseRequest(req, Empleado.class);
            List<String> errores = validarEmpleado(empleado);

            if (!errores.isEmpty()) {
                sendJson(resp, 400, new ErrorResponse().errores(errores));
                return;
            }

            EmpleadoResponse dto = empleadoService.crearEmpleado(empleado);
            sendJson(resp, 201, dto);
        } catch (RutDuplicadoException e) {
            log.warn("RUT duplicado: {}", e.getMessage());
            sendJson(resp, 400, new ErrorResponse().addErroresItem(e.getMessage()));
        } catch (Exception e) {
            log.error("Error en POST", e);
            sendJson(resp, 500, new ErrorResponse().addErroresItem("Error interno del servidor"));
        }
    }

    /**
     * Elimina un empleado por el ID indicado en la URL.
     *
     * @param req  peticion HTTP con path {@code /api/empleados/{id}}
     * @param resp respuesta HTTP con status 200, 400 o 404
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        log.info("DELETE /api/empleados");
        Long id = extractIdFromPath(req);
        if (id == null) {
            sendJson(resp, 400, new ErrorResponse().addErroresItem("Debe proporcionar un ID válido"));
            return;
        }
        if (!empleadoService.eliminarEmpleado(id)) {
            sendJson(resp, 404, new ErrorResponse().addErroresItem("No se encontró empleado con ID " + id));
            return;
        }
        sendJson(resp, 200, new MensajeResponse().mensaje("Empleado eliminado correctamente"));
    }

    /**
     * Ejecuta las validaciones de Bean Validation sobre el empleado.
     *
     * @param empleado entidad a validar
     * @return lista de mensajes de error, vacia si todo es valido
     */
    private List<String> validarEmpleado(Empleado empleado) {
        Set<ConstraintViolation<Empleado>> violations = validator.validate(empleado);
        return violations.stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.toList());
    }

    /**
     * Deserializa el body JSON de la peticion al tipo indicado.
     *
     * @param req   peticion HTTP
     * @param clazz clase destino
     * @param <T>   tipo generico
     * @return instancia deserializada
     * @throws Exception si el JSON es invalido
     */
    private <T> T parseRequest(HttpServletRequest req, Class<T> clazz) throws Exception {
        String body = req.getReader().lines().collect(Collectors.joining());
        return objectMapper.readValue(body, clazz);
    }

    /**
     * Extrae el ID numerico del path de la peticion (ej: {@code /1} → {@code 1L}).
     *
     * @param req peticion HTTP
     * @return ID extraido o {@code null} si no es valido
     */
    private Long extractIdFromPath(HttpServletRequest req) {
        String path = req.getPathInfo();
        if (path == null || path.equals("/")) return null;
        try {
            return Long.parseLong(path.substring(1));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Serializa un objeto a JSON y lo escribe en la respuesta HTTP.
     *
     * @param resp   respuesta HTTP
     * @param status codigo de estado HTTP
     * @param data   objeto a serializar
     */
    private void sendJson(HttpServletResponse resp, int status, Object data) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write(objectMapper.writeValueAsString(data));
    }
}
