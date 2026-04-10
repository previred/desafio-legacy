package com.desafio.controller;

import com.desafio.dto.EmpleadoDTO;
import com.desafio.dto.ErrorResponse;
import com.desafio.service.EmpleadoService;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Servlet nativo que maneja las operaciones CRUD para Empleados.
 * NO usa @RestController ni Spring Web MVC.
 * Se registra mediante ServletRegistrationBean en la clase de configuración.
 *
 * Endpoints:
 *   GET    /api/empleados      → Lista todos los empleados
 *   GET    /api/empleados/{id} → Obtiene un empleado por ID
 *   POST   /api/empleados      → Crea un nuevo empleado
 *   DELETE /api/empleados/{id} → Elimina un empleado por ID
 */
public class EmpleadoServlet extends HttpServlet {

    private final EmpleadoService empleadoService;
    private final ObjectMapper objectMapper;

    public EmpleadoServlet(EmpleadoService empleadoService, ObjectMapper objectMapper) {
        this.empleadoService = empleadoService;
        this.objectMapper = objectMapper;
    }

    /**
     * GET /api/empleados       → Lista todos
     * GET /api/empleados/{id}  → Busca por ID
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        configurarCORS(resp);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String pathInfo = req.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /api/empleados → listar todos
                List<EmpleadoDTO> empleados = empleadoService.listarTodos();
                resp.setStatus(HttpServletResponse.SC_OK);
                objectMapper.writeValue(resp.getOutputStream(), empleados);
            } else {
                // GET /api/empleados/{id}
                Long id = extraerIdDePath(pathInfo);
                empleadoService.buscarPorId(id)
                        .map(dto -> {
                            try {
                                resp.setStatus(HttpServletResponse.SC_OK);
                                objectMapper.writeValue(resp.getOutputStream(), dto);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            return dto;
                        })
                        .orElseGet(() -> {
                            enviarError(resp, HttpServletResponse.SC_NOT_FOUND,
                                    "No encontrado",
                                    Collections.singletonList("Empleado con ID " + id + " no encontrado."));
                            return null;
                        });
            }
        } catch (NumberFormatException e) {
            enviarError(resp, HttpServletResponse.SC_BAD_REQUEST,
                    "ID inválido",
                    Collections.singletonList("El ID proporcionado no es un número válido."));
        } catch (Exception e) {
            enviarError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error interno",
                    Collections.singletonList(e.getMessage()));
        }
    }

    /**
     * POST /api/empleados → Crea un nuevo empleado
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        configurarCORS(resp);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String body = leerBody(req);

            if (body == null || body.trim().isEmpty()) {
                enviarError(resp, HttpServletResponse.SC_BAD_REQUEST,
                        "Cuerpo vacío",
                        Collections.singletonList("El cuerpo de la solicitud no puede estar vacío."));
                return;
            }

            EmpleadoDTO dto = objectMapper.readValue(body, EmpleadoDTO.class);
            EmpleadoDTO creado = empleadoService.crearEmpleado(dto);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            objectMapper.writeValue(resp.getOutputStream(), creado);

        } catch (EmpleadoService.ValidacionException e) {
            enviarError(resp, HttpServletResponse.SC_BAD_REQUEST,
                    "Errores de validación",
                    e.getErrores());
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            enviarError(resp, HttpServletResponse.SC_BAD_REQUEST,
                    "JSON inválido",
                    Collections.singletonList("El formato del JSON enviado es inválido: " + e.getOriginalMessage()));
        } catch (Exception e) {
            enviarError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error interno",
                    Collections.singletonList(e.getMessage()));
        }
    }

    /**
     * DELETE /api/empleados/{id} → Elimina un empleado
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        configurarCORS(resp);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String pathInfo = req.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                enviarError(resp, HttpServletResponse.SC_BAD_REQUEST,
                        "ID requerido",
                        Collections.singletonList("Debe proporcionar el ID del empleado a eliminar."));
                return;
            }

            Long id = extraerIdDePath(pathInfo);
            boolean eliminado = empleadoService.eliminarEmpleado(id);

            if (eliminado) {
                resp.setStatus(HttpServletResponse.SC_OK);
                objectMapper.writeValue(resp.getOutputStream(),
                        Collections.singletonMap("mensaje", "Empleado con ID " + id + " eliminado exitosamente."));
            } else {
                enviarError(resp, HttpServletResponse.SC_NOT_FOUND,
                        "No encontrado",
                        Collections.singletonList("Empleado con ID " + id + " no encontrado."));
            }
        } catch (NumberFormatException e) {
            enviarError(resp, HttpServletResponse.SC_BAD_REQUEST,
                    "ID inválido",
                    Collections.singletonList("El ID proporcionado no es un número válido."));
        } catch (Exception e) {
            enviarError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error interno",
                    Collections.singletonList(e.getMessage()));
        }
    }

    /**
     * Maneja las solicitudes OPTIONS para CORS preflight.
     */
    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        configurarCORS(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    // --- Métodos utilitarios ---

    private Long extraerIdDePath(String pathInfo) {
        String idStr = pathInfo.substring(1); // Quita el '/' inicial
        return Long.parseLong(idStr);
    }

    private String leerBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    private void enviarError(HttpServletResponse resp, int status, String mensaje, List<String> errores) {
        try {
            resp.setStatus(status);
            ErrorResponse errorResponse = new ErrorResponse(status, mensaje, errores);
            objectMapper.writeValue(resp.getOutputStream(), errorResponse);
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void configurarCORS(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }
}

