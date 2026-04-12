package com.empleado.app.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.empleado.app.http.base.BaseServlet;
import com.empleado.app.model.EmpleadoRequest;
import com.empleado.app.model.EmpleadoResponse;
import com.empleado.app.service.EmpleadoService;
import com.empleado.app.validator.EmpleadoValidator;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet encargado de exponer la API REST de empleados.
 * Permite realizar operaciones CRUD básicas sobre empleados.
 * Autor: Cristian Palacios
 */
@WebServlet(urlPatterns = "/api/empleados")
public class EmpleadoServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    @Autowired
    private EmpleadoService service;

    /**
     * GET /api/empleados
     * Obtiene la lista de todos los empleados registrados.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        

        execute(resp, () -> {

            // Se obtiene la lista de empleados desde el servicio
            List<EmpleadoResponse> empleados = service.getAll();

            // Se retorna la respuesta en formato JSON
            sendJson(resp, HttpServletResponse.SC_OK, empleados);
        });
    }

    /**
     * POST /api/empleados
     * Crea un nuevo empleado a partir del JSON enviado en el body.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        execute(resp, () -> {

            // Se convierte el JSON del request a objeto Java
            EmpleadoRequest request =
                    mapper.readValue(req.getInputStream(), EmpleadoRequest.class);

            // Validación de datos básicos del empleado
            EmpleadoValidator.validarCamposBasicos(request);

            // Se crea el empleado en la base de datos
            service.create(request);

            // Respuesta de confirmación
            sendJson(resp,
                    HttpServletResponse.SC_CREATED,
                    Map.of("message", "Empleado creado correctamente"));
        });
    }

    /**
     * DELETE /api/empleados?id={id}
     * Elimina un empleado por su ID.
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        execute(resp, () -> {

            // Se obtiene el ID del empleado desde los parámetros de la URL
            Long id = Long.parseLong(req.getParameter("id"));

            // Se elimina el empleado
            service.delete(id);

            // Respuesta de confirmación
            sendJson(resp,
                    HttpServletResponse.SC_OK,
                    Map.of("message", "Empleado eliminado"));
        });
    }
}