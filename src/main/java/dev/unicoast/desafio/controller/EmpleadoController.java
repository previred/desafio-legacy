package dev.unicoast.desafio.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.unicoast.desafio.exception.GlobalExceptionHandler;
import dev.unicoast.desafio.model.dto.EmpleadoDTO;
import dev.unicoast.desafio.service.EmpleadoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "empleadoController", urlPatterns = "/api/empleados")
public class EmpleadoController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(EmpleadoController.class);
    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final String CHARSET_UTF8 = "UTF-8";

    private EmpleadoService empleadoService;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public void init() throws ServletException {
        LOG.info("init | Inicializando EmpleadoController...");
        ApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
        this.empleadoService = context.getBean(EmpleadoService.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            LOG.info("doGet | IN GET /api/empleados");
            List<EmpleadoDTO> empleados = empleadoService.listarEmpleados();

            prepararRespuesta(resp);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(empleados));

            LOG.info("doGet | OUT GET /api/empleados -> 200 OK ({} registros)", empleados.size());
        } catch (Exception e) {
            manejarError(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            LOG.info("doPost | IN POST /api/empleados");
            String body = req.getReader().lines().collect(Collectors.joining());

            EmpleadoDTO nuevoEmpleado = gson.fromJson(body, EmpleadoDTO.class);
            empleadoService.registrarEmpleado(nuevoEmpleado);

            prepararRespuesta(resp);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(gson.toJson(Collections.singletonMap("mensaje", "Registro de empleado guardado")));

            LOG.info("doPost | OUT POST /api/empleados -> 201 CREATED");
        } catch (Exception e) {
            manejarError(resp, e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String idParam = req.getParameter("id");
            LOG.info("doDelete | IN DELETE /api/empleados id={}", idParam);

            empleadoService.eliminarEmpleado(idParam);

            prepararRespuesta(resp);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(Collections.singletonMap("mensaje", "Registro de empleado eliminado")));

            LOG.info("doDelete | OUT DELETE /api/empleados id={} -> 200 OK", idParam);
        } catch (Exception e) {
            manejarError(resp, e);
        }
    }

    private void prepararRespuesta(HttpServletResponse resp) {
        resp.setContentType(CONTENT_TYPE_JSON);
        resp.setCharacterEncoding(CHARSET_UTF8);
    }

    private void manejarError(HttpServletResponse resp, Exception e) {
        try {
            GlobalExceptionHandler.handle(resp, e);
        } catch (IOException ioEx) {
            LOG.error("Error al escribir respuesta HTTP de error", ioEx);
        }
    }
}
