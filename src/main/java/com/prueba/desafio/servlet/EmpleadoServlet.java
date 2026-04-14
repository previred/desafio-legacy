package com.prueba.desafio.servlet;

import com.prueba.desafio.dto.ApiErrorResponse;
import com.prueba.desafio.dto.EmpleadoRequest;
import com.prueba.desafio.model.Empleado;
import com.prueba.desafio.service.BusinessValidationException;
import com.prueba.desafio.service.EmpleadoService;
import com.prueba.desafio.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EmpleadoServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(EmpleadoServlet.class);

    private final EmpleadoService empleadoService;

    public EmpleadoServlet(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            List<Empleado> empleados = empleadoService.listar();
            JsonUtil.writeJson(response, HttpServletResponse.SC_OK, empleados);
        } catch (Exception e) {
            log.error("Error en GET /api/empleados", e);
            JsonUtil.writeJson(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    new ApiErrorResponse(
                            "Error interno al listar empleados",
                            Collections.emptyList()
                    )
            );
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String body = leerBody(request);

            if (body == null || body.trim().isEmpty()) {
                JsonUtil.writeJson(
                        response,
                        HttpServletResponse.SC_BAD_REQUEST,
                        new ApiErrorResponse(
                                "El body de la solicitud es obligatorio",
                                Collections.emptyList()
                        )
                );
                return;
            }

            EmpleadoRequest empleadoRequest = JsonUtil.fromJson(body, EmpleadoRequest.class);
            Empleado empleadoGuardado = empleadoService.guardar(empleadoRequest);

            Map<String, Object> result = new HashMap<String, Object>();
            result.put("message", "Empleado creado correctamente");
            result.put("data", empleadoGuardado);

            JsonUtil.writeJson(response, HttpServletResponse.SC_CREATED, result);

        } catch (BusinessValidationException e) {
            log.warn("Error de validación en POST /api/empleados: {}", e.getMessage());
            JsonUtil.writeJson(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    new ApiErrorResponse(e.getMessage(), e.getErrors())
            );
        } catch (Exception e) {
            log.error("Error en POST /api/empleados", e);
            JsonUtil.writeJson(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    new ApiErrorResponse(
                            "Error interno al guardar empleado",
                            Collections.emptyList()
                    )
            );
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Long id = obtenerIdDesdeRequest(request);

            if (id == null) {
                JsonUtil.writeJson(
                        response,
                        HttpServletResponse.SC_BAD_REQUEST,
                        new ApiErrorResponse(
                                "Debe especificar el id en la URL (/api/empleados/1) o como query param (/api/empleados?id=1)",
                                Collections.emptyList()
                        )
                );
                return;
            }

            boolean eliminado = empleadoService.eliminar(id);

            if (!eliminado) {
                JsonUtil.writeJson(
                        response,
                        HttpServletResponse.SC_NOT_FOUND,
                        new ApiErrorResponse(
                                "No existe un empleado con el id enviado",
                                Collections.emptyList()
                        )
                );
                return;
            }

            Map<String, Object> result = new HashMap<String, Object>();
            result.put("message", "Empleado eliminado correctamente");
            result.put("id", id);

            JsonUtil.writeJson(response, HttpServletResponse.SC_OK, result);

        } catch (NumberFormatException e) {
            JsonUtil.writeJson(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    new ApiErrorResponse(
                            "El id debe ser numérico",
                            Collections.emptyList()
                    )
            );
        } catch (Exception e) {
            log.error("Error en DELETE /api/empleados", e);

            JsonUtil.writeJson(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    new ApiErrorResponse(
                            "Error interno al eliminar empleado",
                            Collections.emptyList()
                    )
            );
        }
    }

    private String leerBody(HttpServletRequest request) throws IOException {
        BufferedReader reader = request.getReader();
        return reader.lines().collect(Collectors.joining(System.lineSeparator()));
    }

    private Long obtenerIdDesdeRequest(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();

        if (pathInfo != null && !pathInfo.trim().isEmpty() && !"/".equals(pathInfo.trim())) {
            String idStr = pathInfo.substring(1).trim();
            if (!idStr.isEmpty()) {
                return Long.parseLong(idStr);
            }
        }

        String idParam = request.getParameter("id");
        if (idParam != null && !idParam.trim().isEmpty()) {
            return Long.parseLong(idParam.trim());
        }

        return null;
    }
}