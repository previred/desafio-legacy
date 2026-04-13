package com.desafio.legacy.controllers;

import com.desafio.legacy.entities.Empleado;
import com.desafio.legacy.models.EmpleadoDTO;
import com.desafio.legacy.services.EmpleadoService;
import com.desafio.legacy.validations.ValidacionesCampos;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EmpleadoController extends HttpServlet {

    @Autowired
    private EmpleadoService empleadoService;

    @Autowired
    private DataSource dataSource;

    @Override
    public void init(ServletConfig config) throws ServletException {
        try (Connection conn = dataSource.getConnection()) {
            String createTable = "CREATE TABLE IF NOT EXISTS empleados (" +
                    "id LONG AUTO_INCREMENT PRIMARY KEY, " +
                    "nombre VARCHAR(255), " +
                    "apellido VARCHAR(255), " +
                    "rut VARCHAR(20) UNIQUE, " +
                    "cargo VARCHAR(255), " +
                    "salario DOUBLE, " +
                    "bono DOUBLE DEFAULT 0, " +
                    "descuento DOUBLE DEFAULT 0, " +
                    "activo BOOLEAN DEFAULT TRUE) ";
            conn.createStatement().execute(createTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            List<EmpleadoDTO> listaEmpleados = empleadoService.listar();
            System.out.println("Cantidad de empleados recuperados: " + listaEmpleados.size());

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(listaEmpleados);

            resp.getWriter().write(json);
            resp.setStatus(HttpServletResponse.SC_OK);
        }catch (Exception e){
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"Error al obtener empleados: " + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        ObjectMapper mapper = new ObjectMapper();

        try {
            Empleado empleado = mapper.readValue(req.getInputStream(), Empleado.class);

            Map<String, String> errores = ValidacionesCampos.validacionCampos(empleado);

            if (!errores.isEmpty()) {
                resp.setStatus(400);
                resp.setContentType("application/json");
                resp.getWriter().write(mapper.writeValueAsString(errores));
                return;
            }

            empleadoService.guardar(empleado);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write("{\"message\": \"Empleado creado con éxito\", \"id\": " + empleado.getId() + "}");

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("rut_dni", e.getMessage());
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String pathInfo = req.getPathInfo();

        try {
            if (pathInfo != null && pathInfo.length() > 1) {
                // Quitamos la barra "/" y convertimos a Long
                Long id = Long.parseLong(pathInfo.substring(1));

                empleadoService.eliminar(id);

                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\": \"ID no proporcionado en la URL\"}");
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"ID inválido\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
