package cl.previred.desafio.servlet;

import cl.previred.desafio.dto.EmpleadoRequest;
import cl.previred.desafio.dto.ErrorResponse;
import cl.previred.desafio.dto.ValidationError;
import cl.previred.desafio.model.Empleado;
import cl.previred.desafio.service.EmpleadoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOG = LoggerFactory.getLogger(EmpleadoServlet.class);

    private final EmpleadoService empleadoService;
    private final ObjectMapper objectMapper;

    public EmpleadoServlet(EmpleadoService empleadoService, ObjectMapper objectMapper) {
        this.empleadoService = empleadoService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LOG.debug("GET /api/empleados - Iniciando solicitud");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        List<Empleado> empleados = empleadoService.getAllEmpleados();
        LOG.debug("GET /api/empleados - Retornando {} empleados", empleados.size());
        resp.getWriter().write(objectMapper.writeValueAsString(empleados));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LOG.debug("POST /api/empleados - Iniciando solicitud");
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            EmpleadoRequest request = objectMapper.readValue(req.getInputStream(), 
            EmpleadoRequest.class);
            LOG.debug("POST /api/empleados - Request parsed: RUT={}", request.getRut());
            List<ValidationError> errores = empleadoService.crearEmpleado(request);

            if (!errores.isEmpty()) {
                LOG.warn("POST /api/empleados - Validation errors: {}", errores);
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                ErrorResponse errorResponse = new ErrorResponse(errores);
                resp.getWriter().write(objectMapper.writeValueAsString(errorResponse));
                return;
            }

            List<Empleado> empleados = empleadoService.getAllEmpleados();
            Empleado ultimoCreado = empleados.isEmpty() ? null : empleados.get(empleados.size() - 1);
            LOG.info("POST /api/empleados - Empleado creado exitosamente");
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(objectMapper.writeValueAsString(ultimoCreado));

        } catch (Exception e) {
            LOG.error("POST /api/empleados - Error al procesar solicitud", e);
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
        LOG.debug("DELETE /api/empleados?id={} - Iniciando solicitud", idParam);

        if (idParam == null || idParam.trim().isEmpty()) {
            LOG.warn("DELETE /api/empleados - ID no proporcionado");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"ID es requerido\"}");
            return;
        }

        try {
            Long id = Long.parseLong(idParam);
            boolean eliminado = empleadoService.eliminarEmpleado(id);

            if (eliminado) {
                LOG.info("DELETE /api/empleados?id={} - Empleado eliminado", id);
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                LOG.warn("DELETE /api/empleados?id={} - Empleado no encontrado", id);
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Empleado no encontrado\"}");
            }
        } catch (NumberFormatException e) {
            LOG.warn("DELETE /api/empleados - ID invalido: {}", idParam);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"ID invalido\"}");
        }
    }
}