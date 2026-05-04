package com.mindgrid.empleados.adapters.inbound.web.servlet;

import com.google.gson.Gson;
import com.mindgrid.empleados.adapters.inbound.web.exception.WebExceptionHandler;
import com.mindgrid.empleados.adapters.inbound.web.mapper.EmpleadoWebMapper;
import com.mindgrid.empleados.adapters.inbound.web.request.EmpleadoRequest;
import com.mindgrid.empleados.application.usecase.GestionEmpleadoUseCase;
import com.mindgrid.empleados.domain.exception.BusinessException;
import com.mindgrid.empleados.domain.model.Empleado;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@WebServlet("/api/empleados")
public class EmpleadoServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(EmpleadoServlet.class.getName());
    private static final Gson GSON = new Gson();

    private GestionEmpleadoUseCase useCase;

    @Override
    public void init() {
        useCase = (GestionEmpleadoUseCase) getServletContext().getAttribute("gestionEmpleadoUseCase");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        LOGGER.info("GET /api/empleados");
        try {
            List<Empleado> empleados = useCase.listarEmpleados();
            List<Object> responseBody = empleados.stream()
                .map(EmpleadoWebMapper::toResponse)
                .collect(Collectors.toList());

            writeJsonResponse(response, HttpServletResponse.SC_OK, responseBody);

        } catch (BusinessException e) {
            LOGGER.warning("Error de negocio en GET: " + e.getMessage());
            WebExceptionHandler.handle(response, e);
        } catch (Exception e) {
            LOGGER.severe("Error inesperado en GET: " + e.getMessage());
            WebExceptionHandler.handleGeneric(response, "Error interno del servidor");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        LOGGER.info("POST /api/empleados");
        try {
            String body = leerBody(request);
            EmpleadoRequest empleadoRequest = GSON.fromJson(body, EmpleadoRequest.class);

            if (empleadoRequest == null) {
                throw new BusinessException("El cuerpo de la petición es inválido o está vacío");
            }

            Empleado domainEmpleado = EmpleadoWebMapper.toDomain(empleadoRequest);
            Empleado creado = useCase.agregarEmpleado(domainEmpleado);

            writeJsonResponse(response, HttpServletResponse.SC_CREATED, EmpleadoWebMapper.toResponse(creado));

        } catch (BusinessException e) {
            LOGGER.warning("Error de negocio en POST: " + e.getMessage());
            WebExceptionHandler.handle(response, e);
        } catch (Exception e) {
            LOGGER.severe("Error inesperado en POST: " + e.getMessage());
            WebExceptionHandler.handleGeneric(response, "Error interno del servidor");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        LOGGER.info("DELETE /api/empleados");
        try {
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.trim().isEmpty()) {
                throw new BusinessException("El parámetro 'id' es requerido");
            }

            long id;
            try {
                id = Long.parseLong(idParam.trim());
            } catch (NumberFormatException e) {
                throw new BusinessException("El parámetro 'id' debe ser un número válido");
            }

            useCase.eliminarEmpleado(id);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);

        } catch (BusinessException e) {
            LOGGER.warning("Error de negocio en DELETE: " + e.getMessage());
            WebExceptionHandler.handle(response, e);
        } catch (Exception e) {
            LOGGER.severe("Error inesperado en DELETE: " + e.getMessage());
            WebExceptionHandler.handleGeneric(response, "Error interno del servidor");
        }
    }

    private String leerBody(HttpServletRequest request) throws IOException {
        try (BufferedReader reader = request.getReader()) {
            return reader.lines().collect(Collectors.joining());
        }
    }

    private void writeJsonResponse(HttpServletResponse response, int status, Object body) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(GSON.toJson(body));
    }
}
