package cl.previred.desafio.servlet;

import cl.previred.desafio.dto.EmpleadoRequest;
import cl.previred.desafio.dto.ErrorResponse;
import cl.previred.desafio.dto.ValidationError;
import cl.previred.desafio.model.Empleado;
import cl.previred.desafio.service.EmpleadoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@WebServlet("/api/empleados")
public class EmpleadoServlet extends HttpServlet {

    private final EmpleadoService empleadoService;
    private final ObjectMapper objectMapper;

    public EmpleadoServlet(EmpleadoService empleadoService, ObjectMapper objectMapper) {
        this.empleadoService = empleadoService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        List<Empleado> empleados = empleadoService.getAllEmpleados();
        resp.getWriter().write(objectMapper.writeValueAsString(empleados));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            EmpleadoRequest request = objectMapper.readValue(req.getInputStream(), 
            EmpleadoRequest.class);
            List<ValidationError> errores = empleadoService.crearEmpleado(request);

            if (!errores.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                ErrorResponse errorResponse = new ErrorResponse(errores);
                resp.getWriter().write(objectMapper.writeValueAsString(errorResponse));
                return;
            }

            List<Empleado> empleados = empleadoService.getAllEmpleados();
            Empleado ultimoCreado = empleados.isEmpty() ? null : empleados.get(empleados.size() - 1);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(objectMapper.writeValueAsString(ultimoCreado));

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ErrorResponse errorResponse = new ErrorResponse(
                    Arrays.asList(new ValidationError("general", 
                    "Error al procesar la solicitud: " + e.getMessage()))
            );
            resp.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idParam = req.getParameter("id");

        if (idParam == null || idParam.trim().isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"ID es requerido\"}");
            return;
        }

        try {
            Long id = Long.parseLong(idParam);
            boolean eliminado = empleadoService.eliminarEmpleado(id);

            if (eliminado) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Empleado no encontrado\"}");
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"ID invalido\"}");
        }
    }
}