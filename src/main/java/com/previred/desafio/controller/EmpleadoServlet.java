package com.previred.desafio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.previred.desafio.exception.BusinessException;
import com.previred.desafio.model.Empleado;
import com.previred.desafio.service.EmpleadoService;

import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/empleados")
public class EmpleadoServlet extends HttpServlet {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private EmpleadoService service;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.service = WebApplicationContextUtils
                .getRequiredWebApplicationContext(config.getServletContext())
                .getBean(EmpleadoService.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            java.util.List<Empleado> empleados = service.listarTodos();
            resp.getWriter().write(objectMapper.writeValueAsString(empleados));
        } catch (Exception e) {
            enviarError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al listar: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            Empleado nuevo = objectMapper.readValue(req.getReader(), Empleado.class);

            service.registrarEmpleado(nuevo);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write("{\"mensaje\": \"Empleado creado exitosamente\"}");

        } catch (BusinessException e) {

            enviarError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            enviarError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error interno: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idParam = req.getParameter("id");

        if (idParam == null) {
            enviarError(resp, HttpServletResponse.SC_BAD_REQUEST, "Se requiere el ID del empleado");
            return;
        }

        try {
            Long id = Long.parseLong(idParam);
            service.eliminarEmpleado(id);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (Exception e) {
            enviarError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al eliminar");
        }
    }

    private void enviarError(HttpServletResponse resp, int codigo, String mensaje) throws IOException {
        resp.setStatus(codigo);
        Map<String, Object> errorMap = new HashMap<>();
        errorMap.put("status", "error");
        errorMap.put("mensaje", mensaje);
        resp.getWriter().write(objectMapper.writeValueAsString(errorMap));
    }
}
