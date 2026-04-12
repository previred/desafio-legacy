package cl.previred.desafio.servlet;

import cl.previred.desafio.dao.EmpleadoDao;
import cl.previred.desafio.model.Empleado;
import cl.previred.desafio.service.EmpleadoValidator;
import cl.previred.desafio.util.RutUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "empleadoServlet", urlPatterns = "/api/empleados")
public class EmpleadoServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(EmpleadoServlet.class);

    private transient EmpleadoDao empleadoDao;
    private transient EmpleadoValidator validator;
    private transient ObjectMapper objectMapper;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        org.springframework.context.ApplicationContext ctx =
                WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext());
        this.empleadoDao = ctx.getBean(EmpleadoDao.class);
        this.validator = ctx.getBean(EmpleadoValidator.class);
        this.objectMapper = ctx.getBean(ObjectMapper.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            List<Empleado> empleados = empleadoDao.findAll();
            writeJson(resp, HttpServletResponse.SC_OK, empleados);
        } catch (Exception ex) {
            log.error("Error al listar empleados", ex);
            writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error interno al listar empleados.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Empleado empleado;
            try {
                empleado = objectMapper.readValue(req.getInputStream(), Empleado.class);
            } catch (Exception parseEx) {
                log.warn("JSON invalido en POST /api/empleados: {}", parseEx.getMessage());
                writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "JSON invalido: " + parseEx.getMessage());
                return;
            }

            List<String> errors = validator.validate(empleado);
            if (!errors.isEmpty()) {
                Map<String, Object> payload = new HashMap<>();
                payload.put("errors", errors);
                payload.put("empleado", empleado);
                writeJson(resp, HttpServletResponse.SC_BAD_REQUEST, payload);
                return;
            }

            empleado.setRut(RutUtil.normalize(empleado.getRut()));
            Empleado creado = empleadoDao.insert(empleado);
            writeJson(resp, HttpServletResponse.SC_CREATED, creado);
        } catch (Exception ex) {
            log.error("Error al crear empleado", ex);
            writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error interno al crear empleado.");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String idParam = req.getParameter("id");
            if (idParam == null || idParam.trim().isEmpty()) {
                writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "Parametro 'id' es obligatorio.");
                return;
            }
            long id;
            try {
                id = Long.parseLong(idParam);
            } catch (NumberFormatException nfe) {
                writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "Parametro 'id' debe ser numerico.");
                return;
            }

            int deleted = empleadoDao.deleteById(id);
            if (deleted == 0) {
                writeError(resp, HttpServletResponse.SC_NOT_FOUND, "No existe empleado con id " + id + ".");
                return;
            }
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (Exception ex) {
            log.error("Error al eliminar empleado", ex);
            writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error interno al eliminar empleado.");
        }
    }

    private void writeJson(HttpServletResponse resp, int status, Object body) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        objectMapper.writeValue(resp.getOutputStream(), body);
    }

    private void writeError(HttpServletResponse resp, int status, String message) throws IOException {
        writeJson(resp, status, Collections.singletonMap("errors", Collections.singletonList(message)));
    }
}
