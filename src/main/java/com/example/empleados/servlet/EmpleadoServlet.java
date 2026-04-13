package com.example.empleados.servlet;

import com.example.empleados.dao.EmpleadoDAO;
import com.example.empleados.model.Empleado;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;


@WebServlet("/api/empleados")
public class EmpleadoServlet extends HttpServlet {


    private final EmpleadoDAO empleadoDAO = new EmpleadoDAO();
    private static final List<Empleado> empleados = new ArrayList<>();
    private static final AtomicLong SEQUENCE = new AtomicLong(1);



    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        ObjectMapper mapper = new ObjectMapper();

        try {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");

            mapper.writeValue(resp.getOutputStream(), empleadoDAO.findAll());

        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }




    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        List<String> errores = new ArrayList<>();

        try {
            Empleado nuevo = mapper.readValue(req.getInputStream(), Empleado.class);

            if (nuevo.getNombre() == null || nuevo.getNombre().isEmpty())
                errores.add("Nombre obligatorio");

            if (nuevo.getApellido() == null || nuevo.getApellido().isEmpty())
                errores.add("Apellido obligatorio");

            if (nuevo.getRut() == null || nuevo.getRut().isEmpty())
                errores.add("RUT/DNI obligatorio");

            if (nuevo.getSalario() < 400000)
                errores.add("Salario base no puede ser menor a 400.000");

            if (empleadoDAO.existsByRut(nuevo.getRut()))
                errores.add("RUT/DNI duplicado");

            if (!errores.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                mapper.writeValue(resp.getOutputStream(), errores);
                return;
            }

            empleadoDAO.save(nuevo);
            resp.setStatus(HttpServletResponse.SC_CREATED);

        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }




    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        try {
            Long id = Long.valueOf(req.getParameter("id"));

            boolean eliminado = empleadoDAO.deleteById(id);

            if (!eliminado) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);

        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }



}
