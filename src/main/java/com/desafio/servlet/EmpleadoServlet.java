package com.desafio.servlet;

import com.desafio.model.Empleado;
import com.desafio.service.EmpleadoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.util.List;

@WebServlet("/api/empleados")
public class EmpleadoServlet extends HttpServlet {

    private EmpleadoService service = new EmpleadoService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            BufferedReader reader = req.getReader();
            String body = reader.lines().reduce("", (acc, line) -> acc + line);

            ObjectMapper mapper = new ObjectMapper();
            Empleado emp = mapper.readValue(body, Empleado.class);

            service.create(emp);

            resp.setStatus(HttpServletResponse.SC_CREATED);

        } catch (Exception e) {
            errorHandler(resp, e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            List<Empleado> empleados = service.list();

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(empleados);

            resp.setContentType("application/json");
            resp.getWriter().write(json);

        } catch (Exception e) {
            errorHandler(resp, e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        try {
            String idParam = req.getParameter("id");
            Long id = Long.parseLong(idParam);

            service.delete(id);

            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);

        } catch (Exception e) {
            errorHandler(resp, e);
        }
    }

    private void errorHandler(HttpServletResponse resp, Exception e) {
        try {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json");

            String json = "{\"error\":\"" + e.getMessage() + "\"}";
            resp.getWriter().write(json);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
