package com.danielr.desafio.controller;

import com.danielr.desafio.dto.EmployeeRequestDTO;
import com.danielr.desafio.dto.EmployeeResponseDTO;
import com.danielr.desafio.exception.BusinessException;
import com.danielr.desafio.service.IEmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/api/empleados/*")
public class EmployeeController extends HttpServlet {

    private IEmployeeService employeeService;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        ApplicationContext ctx = WebApplicationContextUtils
                .getRequiredWebApplicationContext(getServletContext());
        this.employeeService = ctx.getBean(IEmployeeService.class);
        this.objectMapper = new ObjectMapper();

        // para LocalDateTime
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        setJsonResponse(resp);

        try {
            List<EmployeeResponseDTO> empleados = employeeService.findAll();
            resp.setStatus(HttpServletResponse.SC_OK); // 200
            resp.getWriter().write(objectMapper.writeValueAsString(empleados));
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al obtener empleados");
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        setJsonResponse(resp);

        try {
            EmployeeRequestDTO dto = objectMapper.readValue(req.getReader(), EmployeeRequestDTO.class);
            EmployeeResponseDTO created = employeeService.save(dto);
            resp.setStatus(HttpServletResponse.SC_CREATED); // 201
            resp.getWriter().write(objectMapper.writeValueAsString(created));
        } catch (BusinessException e) {
            sendErrors(resp, HttpServletResponse.SC_BAD_REQUEST, e.getErrors()); // 400
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al crear empleado"); // 500
        }

    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        setJsonResponse(resp);

        try {
            Long id = extractId(req);
            if (id == null) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Se requiere ID"); // 400
                return;
            }

            if (employeeService.delete(id)) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT); // 204
            } else {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Empleado no encontrado");
            }

        } catch (BusinessException e) {
            sendErrors(resp, HttpServletResponse.SC_BAD_REQUEST, e.getErrors()); // 400
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al eliminar empleado"); // 500
        }

    }

    /**
     * extrae id de la ruta
     */
    private Long extractId(HttpServletRequest req) {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) return null;
        try {
            return Long.parseLong(pathInfo.substring(1));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Set Json y encoding
     */
    private void setJsonResponse(HttpServletResponse resp) {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
    }

    /**
     * Error simple con un mensaje
     */
    private void sendError(HttpServletResponse resp, int status, String message)
            throws IOException {
        resp.setStatus(status);
        resp.getWriter().write(
                objectMapper.writeValueAsString(Map.of("mensaje", message))
        );
    }

    /**
     * Error con lista de mensajes (validaciones)
     */
    private void sendErrors(HttpServletResponse resp, int status, List<String> errors)
            throws IOException {
        resp.setStatus(status);
        resp.getWriter().write(
                objectMapper.writeValueAsString(Map.of("errores", errors))
        );
    }

}
