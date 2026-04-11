package cl.previred.desafio.servlet;

import cl.previred.desafio.dto.EmpleadoRequest;
import cl.previred.desafio.dto.ErrorResponse;
import cl.previred.desafio.dto.ValidationError;
import cl.previred.desafio.exception.ValidationExceptionList;
import cl.previred.desafio.model.Empleado;
import cl.previred.desafio.service.EmpleadoService;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
@WebServlet("/api/empleados/*")
public class EmpleadoServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(EmpleadoServlet.class);

    private final EmpleadoService empleadoService;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    public EmpleadoServlet(EmpleadoService empleadoService, com.fasterxml.jackson.databind.ObjectMapper objectMapper) {
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
            EmpleadoRequest request = objectMapper.readValue(req.getInputStream(), EmpleadoRequest.class);
            LOG.debug("POST /api/empleados - Request parsed: RUT={}", request.getRut());
            empleadoService.crearEmpleado(request);

            List<Empleado> empleados = empleadoService.getAllEmpleados();
            Empleado ultimoCreado = empleados.isEmpty() ? null : empleados.get(empleados.size() - 1);
            LOG.info("POST /api/empleados - Empleado creado exitosamente");
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(objectMapper.writeValueAsString(ultimoCreado));

        } catch (ValidationExceptionList ex) {
            LOG.warn("POST /api/empleados - Validation errors: {}", ex.getErrores());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(objectMapper.writeValueAsString(new ErrorResponse(ex.getErrores())));
        } catch (InvalidFormatException ex) {
            LOG.warn("POST /api/empleados - Formato invalido: {}", ex.getMessage());
            String campo = ex.getPath().isEmpty() ? "json" : ex.getPath().get(0).getFieldName();
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(objectMapper.writeValueAsString(
                    new ErrorResponse(Collections.singletonList(new ValidationError(campo, "Formato invalido para el campo: " + campo)))));
        } catch (MismatchedInputException ex) {
            LOG.warn("POST /api/empleados - JSON invalido: {}", ex.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(objectMapper.writeValueAsString(
                    new ErrorResponse(Collections.singletonList(new ValidationError("json", "JSON invalido o no pudo ser parseado")))));
        } catch (Exception ex) {
            LOG.error("POST /api/empleados - Error al procesar solicitud", ex);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(objectMapper.writeValueAsString(
                    new ErrorResponse(Collections.singletonList(new ValidationError("internal", "Error interno del servidor")))));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            LOG.warn("DELETE /api/empleados - ID no proporcionado");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(objectMapper.writeValueAsString(
                    new ErrorResponse(Collections.singletonList(new ValidationError("id", "ID es requerido")))));
            return;
        }

        String idParam = pathInfo.substring(1);
        LOG.debug("DELETE /api/empleados/{} - Iniciando solicitud", idParam);

        try {
            Long id = Long.parseLong(idParam);
            boolean eliminado = empleadoService.eliminarEmpleado(id);

            if (eliminado) {
                LOG.info("DELETE /api/empleados/{} - Empleado eliminado", id);
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                LOG.warn("DELETE /api/empleados/{} - Empleado no encontrado", id);
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write(objectMapper.writeValueAsString(
                        new ErrorResponse(Collections.singletonList(new ValidationError("id", "Empleado no encontrado")))));
            }
        } catch (NumberFormatException ex) {
            LOG.warn("DELETE /api/empleados - ID invalido: {}", idParam);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(objectMapper.writeValueAsString(
                    new ErrorResponse(Collections.singletonList(new ValidationError("id", "ID invalido")))));
        }
    }
}
