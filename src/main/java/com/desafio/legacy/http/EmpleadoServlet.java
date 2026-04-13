package com.desafio.legacy.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;

import com.desafio.legacy.dto.EmpleadoRequest;
import com.desafio.legacy.dto.EmpleadoResponse;
import com.desafio.legacy.dto.ErrorResponse;
import com.desafio.legacy.exception.BusinessValidationException;
import com.desafio.legacy.exception.ResourceNotFoundException;
import com.desafio.legacy.service.contract.EmpleadoService;

/**
 * Servlet nativo para operaciones de empleados.
 * No usa @RestController.
 * Se registra con ServletRegistrationBean.
 * Endpoints: GET /api/empleados, POST /api/empleados, DELETE /api/empleados/{id}.
 */
public class EmpleadoServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmpleadoServlet.class);

    private final EmpleadoService empleadoService;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    public EmpleadoServlet(EmpleadoService empleadoService, ObjectMapper objectMapper, Validator validator) {
        this.empleadoService = empleadoService;
        this.objectMapper = objectMapper;
        this.validator = validator;
    }

    /**
     * Lista empleados y permite filtrar por query params opcionales.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String term = request.getParameter("q");
            String cargo = request.getParameter("cargo");
            List<EmpleadoResponse> empleados = empleadoService.obtenerEmpleados(term, cargo);
            writeJson(response, HttpServletResponse.SC_OK, empleados);
        } catch (Exception ex) {
            handleUnexpected(response, ex);
        }
    }

    /**
     * Crea un empleado validando formato de entrada y reglas de negocio.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            EmpleadoRequest empleadoRequest = objectMapper.readValue(request.getInputStream(), EmpleadoRequest.class);
            List<String> details = validateRequest(empleadoRequest);
            if (!details.isEmpty()) {
                writeError(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "VALIDATION_ERROR",
                    "Hay errores de validacion",
                    details
                );
                return;
            }

            EmpleadoResponse created = empleadoService.crearEmpleado(empleadoRequest);
            writeJson(response, HttpServletResponse.SC_CREATED, created);
        } catch (JsonProcessingException ex) {
            LOGGER.warn("JSON invalido recibido", ex);
            writeError(
                response,
                HttpServletResponse.SC_BAD_REQUEST,
                "INVALID_JSON",
                "El cuerpo JSON es invalido o no puede parsearse",
                Collections.singletonList("Revise tipos de datos y formato JSON")
            );
        } catch (BusinessValidationException ex) {
            LOGGER.warn("Validacion de negocio fallida: {}", ex.getMessage());
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, ex.getCode(), ex.getMessage(), ex.getDetails());
        } catch (DataIntegrityViolationException ex) {
            LOGGER.warn("Violacion de integridad de datos", ex);
            writeError(
                response,
                HttpServletResponse.SC_BAD_REQUEST,
                "VALIDATION_ERROR",
                "Hay errores de validacion",
                Collections.singletonList("El RUT/DNI ya existe")
            );
        } catch (Exception ex) {
            handleUnexpected(response, ex);
        }
    }

    /**
     * Elimina un empleado por ID recibido en el path.
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long id = extractId(request);
        if (id == null) {
            writeError(
                response,
                HttpServletResponse.SC_BAD_REQUEST,
                "INVALID_PARAMETER",
                "Parametro de ruta invalido",
                Collections.singletonList("Parametro invalido: id")
            );
            return;
        }

        try {
            empleadoService.eliminarEmpleado(id);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (BusinessValidationException ex) {
            LOGGER.warn("Validacion de negocio fallida: {}", ex.getMessage());
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, ex.getCode(), ex.getMessage(), ex.getDetails());
        } catch (ResourceNotFoundException ex) {
            LOGGER.info("Recurso no encontrado: {}", ex.getMessage());
            writeError(
                response,
                HttpServletResponse.SC_NOT_FOUND,
                ex.getCode(),
                ex.getMessage(),
                Collections.<String>emptyList()
            );
        } catch (Exception ex) {
            handleUnexpected(response, ex);
        }
    }

    private List<String> validateRequest(EmpleadoRequest request) {
        Set<ConstraintViolation<EmpleadoRequest>> violations = validator.validate(request);
        if (violations.isEmpty()) {
            return Collections.emptyList();
        }

        return violations.stream()
            .map(ConstraintViolation::getMessage)
            .distinct()
            .collect(Collectors.toList());
    }

    private Long extractId(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.trim().isEmpty() || "/".equals(pathInfo)) {
            return null;
        }

        String normalized = pathInfo.startsWith("/") ? pathInfo.substring(1) : pathInfo;
        if (normalized.contains("/")) {
            return null;
        }

        try {
            return Long.valueOf(normalized);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private void writeJson(HttpServletResponse response, int status, Object body) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        response.setHeader("Pragma", "no-cache");
        objectMapper.writeValue(response.getWriter(), body);
    }

    private void writeError(HttpServletResponse response, int status, String code, String message, List<String> details)
        throws IOException {
        ErrorResponse errorResponse = new ErrorResponse(code, message, details == null ? new ArrayList<String>() : details);
        writeJson(response, status, errorResponse);
    }

    private void handleUnexpected(HttpServletResponse response, Exception ex) throws IOException {
        LOGGER.error("Error inesperado en la API", ex);
        writeError(
            response,
            HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
            "UNEXPECTED_ERROR",
            "Ocurrio un error inesperado",
            Collections.<String>emptyList()
        );
    }
}
